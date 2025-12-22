package com.caffeinatedr4t.tamanbacaan.data

import android.util.Log
import com.caffeinatedr4t.tamanbacaan.api.ApiConfig
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.models.EventNotification
import com.caffeinatedr4t.tamanbacaan.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong
import java.text.SimpleDateFormat
import java. util.Calendar
import com.caffeinatedr4t.tamanbacaan.models.Transaction


/**
 * Repository yang mengambil data dari MongoDB API backend.
 * Menggunakan hybrid pattern:  fetch dari API, maintain local state untuk bookmarks/borrowed.
 */
object BookRepository {
    private const val TAG = "BookRepository"

    private val eventNotifications = mutableListOf<EventNotification>()
    private val nextEventId = AtomicLong(3) // Lanjutkan dari ID 2
    // Local state untuk bookmarks dan borrowed status
    private val localBookState = mutableMapOf<String, LocalBookState>()
    private val pendingRequests = mutableListOf<PendingRequest>()
    // registrationRequests REMOVED (Diganti dengan aktivasi instan)
    private val activeMembers = mutableListOf<User>()

    private val nextRequestId = AtomicLong(3) // Lanjutkan dari ID 2
    private val nextUserId = AtomicLong(103) // Lanjutkan ID anggota setelah M102
    var currentUserId: String? = null

    // Data class untuk menyimpan state lokal buku
    data class LocalBookState(
        var isBookmarked: Boolean = false,
        var isBorrowed: Boolean = false,
        var isAvailable: Boolean = true,
        var borrowedDate: String?  = null,
        var dueDate: String? = null
    )

    /**
     * Apply local state overlay to a book from API
     */
    private fun applyLocalState(book:  Book): Book {
        val state = localBookState[book.id] ?: LocalBookState()
        return book.copy(
            isBookmarked = state.isBookmarked,
            isBorrowed = state.isBorrowed,
            isAvailable = state.isAvailable,
            borrowedDate = state.borrowedDate,
            dueDate = state.dueDate
        )
    }

    suspend fun getAllBooksWithStatus(): List<Book> = withContext(Dispatchers.IO) {
        try {
            // 1. Ambil daftar semua buku
            val booksResponse = ApiConfig.getApiService().getBooks()
            val allBooks = if (booksResponse.isSuccessful) booksResponse.body() ?: emptyList() else emptyList()

            // 2. Jika user sedang login, ambil transaksi dia
            if (currentUserId != null) {
                val transactionsResponse = ApiConfig.getApiService().getUserTransactions(currentUserId!!)
                val userTransactions = if (transactionsResponse.isSuccessful) transactionsResponse.body() ?: emptyList() else emptyList()

                // 3. Gabungkan: Update status buku berdasarkan transaksi terakhir
                allBooks.forEach { book ->
                    // Cari transaksi terakhir untuk buku ini
                    val activeTx = userTransactions.find { tx ->
                        // [PERBAIKAN LOGIKA] Cek tipe data bookId secara manual
                        val rawId = tx.bookId
                        val txBookId = when (rawId) {
                            is Map<*, *> -> rawId["_id"] as? String // Jika bentuknya Object (Populated)
                            is String -> rawId // Jika bentuknya String ID biasa
                            else -> rawId.toString()
                        }

                        // Bandingkan ID
                        txBookId == book.id
                    }

                    if (activeTx != null) {
                        book.status = activeTx.status
                        book.isBorrowed = (activeTx.status == "BORROWED")
                    }
                }
            }
            return@withContext allBooks
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching books", e)
            return@withContext emptyList()
        }
    }

    suspend fun requestBorrowBook(book: Book, userId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // [FIX] Jangan kirim borrowDate (set null), biar Backend yang set tanggal sekarang
            val transaction = Transaction(
                userId = userId,
                bookId = book.id,
                borrowDate = null,
                dueDate = "2025-12-31", // Logic due date bisa diperbaiki nanti
                status = "PENDING"
            )

            val response = ApiConfig.getApiService().borrowBook(transaction)
            if (response.isSuccessful) {
                Log.d(TAG, "Request Success: ${response.body()}")
                return@withContext true
            } else {
                Log.e(TAG, "Request Failed: ${response.errorBody()?.string()}")
                return@withContext false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error request borrow", e)
            return@withContext false
        }
    }

    suspend fun getMyLibraryBooks(userId: String): List<Book> = withContext(Dispatchers.IO) {
        try {
            val response = ApiConfig.getApiService().getUserTransactions(userId)
            if (response.isSuccessful) {
                val transactions = response.body() ?: emptyList()
                val allBooks = getAllBooksWithStatus()

                return@withContext allBooks.filter { book ->
                    transactions.any { tx ->
                        // Logika ekstraksi ID yang sama
                        val rawId = tx.bookId
                        val txBookId = if (rawId is Map<*, *>) rawId["_id"] as? String else rawId.toString()
                        txBookId == book.id
                    }
                }
            }
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun toggleBookmarkStatus(bookId: String): Boolean {
        val state = localBookState. getOrPut(bookId) { LocalBookState() }
        state.isBookmarked = !state.isBookmarked
        return true
    }

    // --- Book CRUD (now using API) ---
    suspend fun addBook(request: CreateBookRequest): Boolean {
        return try {
            val response = ApiConfig.getApiService().createBook(request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAllBooks(): List<Book> {
        val response = ApiConfig.getApiService().getBooks()
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }

    // ✅ UPDATED WITH LOGGING
    suspend fun getBookById(id: String): Book? {
        return try {
            Log.d(TAG, "🔍 Fetching book with ID: $id")
            val response = ApiConfig.getApiService().getBookById(id)

            Log.d(TAG, "📡 Response code: ${response.code()}")
            Log.d(TAG, "📡 Response success: ${response.isSuccessful}")
            Log.d(TAG, "📡 Response body: ${response.body()}")

            if (response. isSuccessful) {
                val book = response.body()
                if (book != null) {
                    Log.d(TAG, "✅ Book parsed successfully:  ${book.title}")
                    applyLocalState(book)
                } else {
                    Log. e(TAG, "❌ Response body is NULL!")
                    null
                }
            } else {
                Log.e(TAG, "❌ Response NOT successful!  Error:  ${response.errorBody()?.string()}")
                null
            }
        } catch (e:  Exception) {
            Log.e(TAG, "💥 EXCEPTION in getBookById: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }

    suspend fun updateBook(updatedBook: Book): Boolean {
        return try {
            val response = ApiConfig.getApiService().updateBook(updatedBook. id, updatedBook)
            if (response.isSuccessful) {
                // Update local state if needed
                val state = localBookState[updatedBook.id]
                if (state != null) {
                    state.isAvailable = updatedBook.isAvailable
                    state.isBorrowed = updatedBook.isBorrowed
                    state. borrowedDate = updatedBook. borrowedDate
                    state. dueDate = updatedBook. dueDate
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    // Local-only update for UI state (doesn't call API)
    fun updateBookLocalState(bookId: String, isAvailable: Boolean, isBorrowed: Boolean) {
        val state = localBookState.getOrPut(bookId) { LocalBookState() }
        state.isAvailable = isAvailable
        state.isBorrowed = isBorrowed
    }

    suspend fun deleteBook(id: String): Boolean {
        return try {
            val response = ApiConfig.getApiService().deleteBook(id)
            if (response.isSuccessful) {
                localBookState.remove(id)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    // --- Transaction Request Management (uses local state) ---
    fun addPendingRequest(book: Book, memberName: String, memberId: String): Boolean {
        if (book.isBorrowed || pendingRequests.any { it. book. id == book.id }) { return false }
        // Update local state to mark as unavailable
        val state = localBookState.getOrPut(book.id) { LocalBookState() }
        state.isAvailable = false
        val request = PendingRequest(requestId = nextRequestId.getAndIncrement().toString(), book = book, memberName = memberName, memberId = memberId, requestDate = "Hari Ini")
        pendingRequests.add(request)
        return true
    }
    fun getPendingRequests(): List<PendingRequest> = pendingRequests.toList()
    fun approveRequest(requestId: String): Boolean {
        val request = pendingRequests. find { it.requestId == requestId } ?: return false

        // Update local state for the book
        val state = localBookState.getOrPut(request. book.id) { LocalBookState() }

        // 1. Dapatkan tanggal hari ini
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale. getDefault())
        val borrowedDate = dateFormat.format(calendar.time)

        // 2. Tambahkan 14 hari untuk jatuh tempo
        calendar.add(Calendar.DAY_OF_YEAR, 14)
        val dueDate = dateFormat.format(calendar.time)

        // 3. Update local state
        state.isAvailable = false
        state.isBorrowed = true
        state.borrowedDate = borrowedDate
        state.dueDate = dueDate

        // Hapus dari daftar permintaan
        val iterator = pendingRequests.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().requestId == requestId) {
                iterator. remove()
                return true
            }
        }
        return false
    }
    fun rejectRequest(requestId: String): Boolean {
        val request = pendingRequests.find { it. requestId == requestId } ?:  return false
        // Update local state to mark as available again
        val state = localBookState.getOrPut(request. book.id) { LocalBookState() }
        state.isAvailable = true
        val iterator = pendingRequests.iterator()
        while (iterator.hasNext()) {
            if (iterator. next().requestId == requestId) {
                iterator.remove()
                return true
            }
        }
        return false
    }

    // --- Registration Management (UPDATED:  Instant Activation) ---

    /**
     * Mendaftarkan anggota baru dan langsung mengaktifkannya (Req: Registrasi -> Langsung Login).
     * Anggota baru memiliki status isVerified = false.
     */
    fun registerNewMember(
        fullName: String,
        nik: String,
        email:  String,
        addressRtRw: String,
        isChild: Boolean,
        parentName: String?
    ): User? {
        // Cek duplikasi NIK/Email (simulasi)
        if (activeMembers.any { it.email == email || it.nik == nik }) {
            return null
        }

        val newUser = User(
            id = "M${nextUserId.getAndIncrement()}",
            fullName = fullName,
            email = email,
            nik = nik,
            addressRtRw = addressRtRw,
            isChild = isChild,
            parentName = parentName,
            status = "Active",
            isVerified = false // Anggota baru selalu belum diverifikasi
        )
        activeMembers.add(newUser)
        return newUser
    }

    // --- Member Management (CRUD + Verification Status) ---
    suspend fun getAllMembers(): List<User> {
        return try {
            val response = ApiConfig.getApiService().getAllMembers()
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Fungsi yang digunakan Admin untuk memverifikasi status RT/RW (Verifikasi Warga).
     */
    suspend fun toggleVerificationStatus(userId: String, currentStatus: Boolean): Boolean {
        return try {
            // Mengirim status kebalikan dari saat ini
            val newStatus = !currentStatus
            val body = mapOf("isVerified" to newStatus)
            val response = ApiConfig.getApiService().updateUserStatus(userId, body)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    fun updateMember(updatedUser: User): Boolean {
        val index = activeMembers.indexOfFirst { it.id == updatedUser.id };
        return if (index != -1) { activeMembers[index] = updatedUser; true } else { false }
    }

    suspend fun deleteMember(userId: String): Boolean {
        return try {
            val response = ApiConfig.getApiService().deleteUser(userId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun checkUserStatus(userId: String): Boolean {
        return try {
            val response = ApiConfig. getApiService().getUserById(userId)
            if (response.isSuccessful) {
                val user = response.body()
                // Return true jika user ada DAN sudah diverifikasi
                user != null && user.isVerified
            } else {
                false // User tidak ditemukan atau error (anggap invalid untuk keamanan)
            }
        } catch (e: Exception) {
            true // Jika error jaringan, jangan logout user (opsional, tergantung kebijakan)
        }
    }

    // [BARU] Fungsi Get Profile untuk Member & Admin
    suspend fun getUserProfile(token: String): User? {
        return try {
            // Backend butuh format "Bearer <token>"
            val authHeader = "Bearer $token"
            val response = ApiConfig.getApiService().getProfile(authHeader)

            if (response.isSuccessful && response.body()?.success == true) {
                response. body()?.user
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateMyProfile(
        token: String,
        request: UpdateProfileRequest
    ): User? {
        return try {
            val response = ApiConfig.getApiService()
                .updateProfile("Bearer $token", request)

            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.user
            } else {
                null
            }
        } catch (e:  Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchPendingRequests(): List<PendingRequest> {
        return try {
            val response = ApiConfig.getApiService().getAllTransactions()
            if (response.isSuccessful) {
                val allTransactions = response.body() ?: emptyList()

                // Filter hanya yang statusnya PENDING
                allTransactions.filter { it.status == "PENDING" }.map { tx ->

                    // Mapping data Transaction -> PendingRequest
                    // Note: Pastikan API mengirim object book & user (populate)
                    // Jika bookId/userId masih String, app mungkin crash atau perlu penyesuaian

                    val bookTitle = if (tx.bookId is Map<*, *>) (tx.bookId["title"] as? String) ?: "Judul Tidak Diketahui" else "Buku ID: ${tx.bookId}"
                    val memberName = if (tx.userId is Map<*, *>) (tx.userId["fullName"] as? String) ?: "Member" else "Member ID: ${tx.userId}"
                    val bookObj = if (tx.bookId is Map<*, *>) Book(id = (tx.bookId["_id"] as? String) ?: "", title = bookTitle) else Book(title = bookTitle)

                    PendingRequest(
                        requestId = tx.id ?: "",
                        book = bookObj,
                        memberName = memberName,
                        memberId = if (tx.userId is Map<*, *>) (tx.userId["_id"] as? String) ?: "" else tx.userId.toString(),
                        requestDate = tx.borrowDate ?: "Hari Ini"
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching pending requests", e)
            emptyList()
        }
    }

    // Fungsi Approve ke API
    suspend fun approveRequestApi(requestId: String): Boolean {
        return try {
            val response = ApiConfig.getApiService().approveTransaction(requestId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    // Fungsi Reject ke API
    suspend fun rejectRequestApi(requestId: String): Boolean {
        return try {
            val response = ApiConfig.getApiService().rejectTransaction(requestId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    fun setUserId(id: String) {
        currentUserId = id
    }

    // --- Admin Data (Tetap) ---
    fun getTopBooks(): Map<String, Int> { return mapOf("To Kill a Mockingbird" to 45, "1984" to 38, "The Great Gatsby" to 32, "Atomic Habits" to 25, "Pride and Prejudice" to 19) }
    fun findMemberByNik(nik: String): User? { return activeMembers.find { it. nik == nik } }
    suspend fun getRecommendations(userId: String): List<Book> = withContext(Dispatchers.IO) {
        try {
            val response = ApiConfig.getApiService().getRecommendations(userId)
            if (response.isSuccessful) {
                val books = response.body()?.data ?: emptyList()
                // Opsional: Cek status availability/borrowed lokal juga jika perlu
                books.forEach { applyLocalState(it) }
                books
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching recommendations", e)
            emptyList()
        }
    }
}