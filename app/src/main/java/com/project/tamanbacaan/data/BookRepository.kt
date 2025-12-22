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
import java.util.Calendar
import com.caffeinatedr4t.tamanbacaan.models.Transaction

/**
 * Repository yang mengambil data dari MongoDB API backend.
 * Menggunakan hybrid pattern: fetch dari API, maintain local state untuk bookmarks/borrowed.
 */
object BookRepository {
    private const val TAG = "BookRepository"

    private val eventNotifications = mutableListOf<EventNotification>()
    private val nextEventId = AtomicLong(3)

    // Local state untuk bookmarks dan borrowed status
    private val localBookState = mutableMapOf<String, LocalBookState>()
    private val pendingRequests = mutableListOf<PendingRequest>()
    private val activeMembers = mutableListOf<User>()

    private val cachedBookmarks = mutableSetOf<String>()

    private val nextRequestId = AtomicLong(3)
    private val nextUserId = AtomicLong(103)
    var currentUserId: String? = null

    // Data class untuk menyimpan state lokal buku
    data class LocalBookState(
        var isBookmarked: Boolean = false,
        var isBorrowed: Boolean = false,
        var isAvailable: Boolean = true,
        var borrowedDate: String? = null,
        var dueDate: String? = null
    )

    /**
     * Apply local state overlay to a book from API.
     * Menggunakan .copy() untuk menghindari error immutability.
     */
    private fun applyLocalState(book: Book): Book {
        val state = localBookState[book.id]


        val isBookmarked = cachedBookmarks.contains(book.id)

        // Buat object baru dengan status bookmark yang benar
        var newBook = book.copy(isBookmarked = isBookmarked)

        // Overlay dengan state lokal lainnya jika ada
        if (state != null) {
            newBook = newBook.copy(
                isBorrowed = state.isBorrowed,
                isAvailable = state.isAvailable,
                borrowedDate = state.borrowedDate,
                dueDate = state.dueDate
            )
        }
        return newBook
    }

    suspend fun getAllBooksWithStatus(): List<Book> = withContext(Dispatchers.IO) {
        try {
            val booksResponse = ApiConfig.getApiService().getBooks()
            val allBooks = if (booksResponse.isSuccessful) booksResponse.body() ?: emptyList() else emptyList()

            if (currentUserId != null) {
                val transactionsResponse = ApiConfig.getApiService().getUserTransactions(currentUserId!!)
                val userTransactions = if (transactionsResponse.isSuccessful) transactionsResponse.body() ?: emptyList() else emptyList()

                try {
                    val userResponse = ApiConfig.getApiService().getUserById(currentUserId!!)
                    if (userResponse.isSuccessful) {
                        val user = userResponse.body()
                        cachedBookmarks.clear()
                        user?.bookmarks?.let { cachedBookmarks.addAll(it) }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching user bookmarks", e)
                }

                return@withContext allBooks.map { book ->
                    // Apply bookmark & local state
                    var processedBook = applyLocalState(book)

                    // Cari transaksi terakhir untuk buku ini
                    val activeTx = userTransactions.find { tx ->
                        val rawId = tx.bookId
                        val txBookId = when (rawId) {
                            is Map<*, *> -> rawId["_id"] as? String
                            is String -> rawId
                            else -> rawId.toString()
                        }
                        txBookId == processedBook.id
                    }

                    if (activeTx != null) {
                        processedBook = processedBook.copy(
                            status = activeTx.status,
                            isBorrowed = (activeTx.status == "BORROWED")
                        )
                    }
                    processedBook
                }
            }

            // Jika tidak login, tetap jalankan applyLocalState untuk guest
            return@withContext allBooks.map { applyLocalState(it) }

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching books", e)
            return@withContext emptyList()
        }
    }

    suspend fun toggleBookmark(bookId: String): Boolean = withContext(Dispatchers.IO) {
        if (currentUserId == null) return@withContext false

        val wasBookmarked = cachedBookmarks.contains(bookId)
        if (wasBookmarked) {
            cachedBookmarks.remove(bookId)
        } else {
            cachedBookmarks.add(bookId)
        }

        try {
            val body = mapOf("bookId" to bookId)
            val response = ApiConfig.getApiService().toggleBookmark(currentUserId!!, body)

            if (!response.isSuccessful) {
                if (wasBookmarked) cachedBookmarks.add(bookId) else cachedBookmarks.remove(bookId)
                return@withContext false
            }
            return@withContext true
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling bookmark API", e)
            if (wasBookmarked) cachedBookmarks.add(bookId) else cachedBookmarks.remove(bookId)
            return@withContext false
        }
    }

    suspend fun requestBorrowBook(book: Book, userId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 14)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dynamicDueDate = dateFormat.format(calendar.time)

            // Buat objek transaksi dengan tanggal dinamis
            val transaction = Transaction(
                userId = userId,
                bookId = book.id,
                borrowDate = null,
                dueDate = dynamicDueDate,
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
        return true
    }

    suspend fun addBook(request: CreateBookRequest): Boolean {
        return try {
            val response = ApiConfig.getApiService().createBook(request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAllBooks(): List<Book> {
        return getAllBooksWithStatus()
    }

    suspend fun getBookById(id: String): Book? {
        return try {
            Log.d(TAG, "🔍 Fetching book with ID: $id")
            val response = ApiConfig.getApiService().getBookById(id)

            Log.d(TAG, "📡 Response code: ${response.code()}")
            Log.d(TAG, "📡 Response success: ${response.isSuccessful}")
            Log.d(TAG, "📡 Response body: ${response.body()}")

            if (response.isSuccessful) {
                val book = response.body()
                if (book != null) {
                    Log.d(TAG, "✅ Book parsed successfully:  ${book.title}")
                    // Apply local state overlay using the return value
                    return applyLocalState(book)
                } else {
                    Log.e(TAG, "❌ Response body is NULL!")
                    null
                }
            } else {
                Log.e(TAG, "❌ Response NOT successful!  Error:  ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "💥 EXCEPTION in getBookById: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }

    suspend fun updateBook(updatedBook: Book): Boolean {
        return try {
            val response = ApiConfig.getApiService().updateBook(updatedBook.id, updatedBook)
            if (response.isSuccessful) {
                // Update local state if needed
                val state = localBookState[updatedBook.id]
                if (state != null) {
                    state.isAvailable = updatedBook.isAvailable
                    state.isBorrowed = updatedBook.isBorrowed
                    state.borrowedDate = updatedBook.borrowedDate
                    state.dueDate = updatedBook.dueDate
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

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

    fun addPendingRequest(book: Book, memberName: String, memberId: String): Boolean {
        if (book.isBorrowed || pendingRequests.any { it.book.id == book.id }) { return false }

        val state = localBookState.getOrPut(book.id) { LocalBookState() }
        state.isAvailable = false

        val request = PendingRequest(
            requestId = nextRequestId.getAndIncrement().toString(),
            book = book,
            memberName = memberName,
            memberId = memberId,
            requestDate = "Hari Ini"
        )
        pendingRequests.add(request)
        return true
    }

    fun getPendingRequests(): List<PendingRequest> = pendingRequests.toList()

    fun approveRequest(requestId: String): Boolean {
        val request = pendingRequests.find { it.requestId == requestId } ?: return false

        val state = localBookState.getOrPut(request.book.id) { LocalBookState() }

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val borrowedDate = dateFormat.format(calendar.time)

        calendar.add(Calendar.DAY_OF_YEAR, 14)
        val dueDate = dateFormat.format(calendar.time)

        state.isAvailable = false
        state.isBorrowed = true
        state.borrowedDate = borrowedDate
        state.dueDate = dueDate

        val iterator = pendingRequests.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().requestId == requestId) {
                iterator.remove()
                return true
            }
        }
        return false
    }

    fun rejectRequest(requestId: String): Boolean {
        val request = pendingRequests.find { it.requestId == requestId } ?: return false

        val state = localBookState.getOrPut(request.book.id) { LocalBookState() }
        state.isAvailable = true

        val iterator = pendingRequests.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().requestId == requestId) {
                iterator.remove()
                return true
            }
        }
        return false
    }

    fun registerNewMember(
        fullName: String,
        nik: String,
        email: String,
        addressRtRw: String,
        isChild: Boolean,
        parentName: String?
    ): User? {
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
            isVerified = false
        )
        activeMembers.add(newUser)
        return newUser
    }

    suspend fun getAllMembers(): List<User> {
        return try {
            val response = ApiConfig.getApiService().getAllMembers()
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun toggleVerificationStatus(userId: String, currentStatus: Boolean): Boolean {
        return try {
            val newStatus = !currentStatus
            val body = mapOf("isVerified" to newStatus)
            val response = ApiConfig.getApiService().updateUserStatus(userId, body)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    fun updateMember(updatedUser: User): Boolean {
        val index = activeMembers.indexOfFirst { it.id == updatedUser.id }
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
            val response = ApiConfig.getApiService().getUserById(userId)
            if (response.isSuccessful) {
                val user = response.body()
                user != null && user.isVerified
            } else {
                false
            }
        } catch (e: Exception) {
            true
        }
    }

    suspend fun getUserProfile(token: String): User? {
        return try {
            val authHeader = "Bearer $token"
            val response = ApiConfig.getApiService().getProfile(authHeader)

            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.user
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
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchPendingRequests(): List<PendingRequest> {
        return try {
            val response = ApiConfig.getApiService().getAllTransactions()
            if (response.isSuccessful) {
                val allTransactions = response.body() ?: emptyList()

                allTransactions.filter { it.status == "PENDING" }.map { tx ->
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

    suspend fun getBorrowedBooksCount(): Int {
        return try {
            val response = ApiConfig.getApiService().getAllTransactions()
            if (response.isSuccessful) {
                val transactions = response.body() ?: emptyList()
                transactions.count { it.status == "BORROWED" }
            } else {
                0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
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

    fun findMemberByNik(nik: String): User? {
        return activeMembers.find { it.nik == nik }
    }

    suspend fun getRecommendations(userId: String): List<Book> = withContext(Dispatchers.IO) {
        try {
            val response = ApiConfig.getApiService().getRecommendations(userId)
            if (response.isSuccessful) {
                val books = response.body()?.data ?: emptyList()
                books.map { applyLocalState(it) }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching recommendations", e)
            emptyList()
        }
    }

    suspend fun submitReview(bookId: String, rating: Double, comment: String): Boolean = withContext(Dispatchers.IO) {
        try {
            if (currentUserId == null) return@withContext false


            val userResponse = ApiConfig.getApiService().getUserById(currentUserId!!)
            val userName = if (userResponse.isSuccessful) userResponse.body()?.fullName ?: "Pengguna" else "Pengguna"

            val request = com.caffeinatedr4t.tamanbacaan.api.model.ReviewRequest(
                userId = currentUserId!!,
                userName = userName,
                rating = rating,
                comment = comment
            )

            val response = ApiConfig.getApiService().addReview(bookId, request)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Error submitting review", e)
            return@withContext false
        }
    }

    suspend fun returnActiveBook(bookId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val userId = currentUserId ?: return@withContext false

            val responseTx = ApiConfig.getApiService().getUserTransactions(userId)
            if (!responseTx.isSuccessful) return@withContext false

            val transactions = responseTx.body() ?: emptyList()

            val activeTx = transactions.find { tx ->
                // Parsing ID karena bisa berupa String atau Object (jika dipopulate)
                val rawId = tx.bookId
                val txBookId = if (rawId is Map<*, *>) rawId["_id"] as? String else rawId.toString()

                txBookId == bookId && tx.status == "BORROWED"
            }

            if (activeTx == null || activeTx.id == null) {
                Log.e(TAG, "Tidak ditemukan transaksi aktif untuk buku ini")
                return@withContext false
            }

            val responseReturn = ApiConfig.getApiService().returnBook(activeTx.id)

            if (responseReturn.isSuccessful) {
                val state = localBookState.getOrPut(bookId) { LocalBookState() }
                state.isBorrowed = false
                state.isAvailable = true
                return@withContext true
            } else {
                return@withContext false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error returning book", e)
            return@withContext false
        }
    }
}