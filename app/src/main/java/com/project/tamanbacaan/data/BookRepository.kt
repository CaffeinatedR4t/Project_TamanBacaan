package com.caffeinatedr4t.tamanbacaan.data

import com.caffeinatedr4t.tamanbacaan.api.ApiConfig
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.models.EventNotification
import com.caffeinatedr4t.tamanbacaan.models.User
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Repository yang mengambil data dari MongoDB API backend.
 * Menggunakan hybrid pattern: fetch dari API, maintain local state untuk bookmarks/borrowed.
 */
object BookRepository {
    private val eventNotifications = mutableListOf<EventNotification>()
    private val nextEventId = AtomicLong(3) // Lanjutkan dari ID 2
    // Local state untuk bookmarks dan borrowed status
    private val localBookState = mutableMapOf<String, LocalBookState>()
    private val pendingRequests = mutableListOf<PendingRequest>()
    // registrationRequests REMOVED (Diganti dengan aktivasi instan)
    private val activeMembers = mutableListOf<User>()

    private val nextRequestId = AtomicLong(3) // Lanjutkan dari ID 2
    private val nextUserId = AtomicLong(103) // Lanjutkan ID anggota setelah M102

    // Data class untuk menyimpan state lokal buku
    data class LocalBookState(
        var isBookmarked: Boolean = false,
        var isBorrowed: Boolean = false,
        var isAvailable: Boolean = true,
        var borrowedDate: String? = null,
        var dueDate: String? = null
    )

    init {
        // Data Sample untuk event
        eventNotifications.add(EventNotification("1", "Bedah Buku 'Laut Bercerita'", "Ikuti bedah buku bersama penulis Leila S. Chudori pada 10 Oktober 2025!", "05/10/2025"))
        eventNotifications.add(EventNotification("2", "Diskon Sewa Buku 50%", "Nikmati diskon 50% untuk semua kategori buku hingga 12 Oktober 2025!", "07/10/2025"))

        // Data Anggota Aktif Default (isVerified disimulasikan)
        activeMembers.add(User(id="M100", fullName="Budi Santoso", email="user@test.com", nik="32xxxxxxxxxxxxxx", addressRtRw = "RT 005/RW 003, Kel. Demo", isChild = false, parentName = null, isVerified = true)) // Sudah diverifikasi
        activeMembers.add(User(id="M101", fullName="Siti Aisyah", email="siti@test.com", nik="32xxxxxxxxxxxxxy", addressRtRw = "RT 004/RW 003, Kel. Demo", isChild = false, parentName = null, isVerified = false)) // Belum diverifikasi
        activeMembers.add(User(id="M102", fullName="Daffa Permana", email="daffa@test.com", nik="32xxxxxxxxxxxxzz", addressRtRw = "RT 002/RW 001, Kel. Demo", isChild = true, parentName = "Ayah Daffa", isVerified = false)) // Belum diverifikasi
    }

    /**
     * Apply local state overlay to a book from API
     */
    private fun applyLocalState(book: Book): Book {
        val state = localBookState[book.id] ?: LocalBookState()
        return book.copy(
            isBookmarked = state.isBookmarked,
            isBorrowed = state.isBorrowed,
            isAvailable = state.isAvailable,
            borrowedDate = state.borrowedDate,
            dueDate = state.dueDate
        )
    }

    fun toggleBookmarkStatus(bookId: String): Boolean {
        val state = localBookState.getOrPut(bookId) { LocalBookState() }
        state.isBookmarked = !state.isBookmarked
        return true
    }

    // Tambahkan fungsi-fungsi ini di dalam BookRepository
    fun getAllEvents(): List<EventNotification> = eventNotifications.toList()

    fun addEvent(title: String, message: String) {
        val newEvent = EventNotification(
            id = nextEventId.getAndIncrement().toString(),
            title = title,
            message = message,
            date = "15/10/2025" // Tanggal hari ini (simulasi)
        )
        eventNotifications.add(0, newEvent) // Tambah di paling atas
    }

    // --- Book CRUD (now using API) ---
    suspend fun addBook(newBook: Book): Boolean {
        return try {
            val response = ApiConfig.getApiService().createBook(newBook)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAllBooks(): List<Book> {
        return try {
            val response = ApiConfig.getApiService().getAllBooks()
            if (response.isSuccessful) {
                response.body()?.map { applyLocalState(it) } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getBookById(id: String): Book? {
        return try {
            val response = ApiConfig.getApiService().getBookById(id)
            if (response.isSuccessful) {
                response.body()?.let { applyLocalState(it) }
            } else {
                null
            }
        } catch (e: Exception) {
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
        if (book.isBorrowed || pendingRequests.any { it.book.id == book.id }) { return false }
        // Update local state to mark as unavailable
        val state = localBookState.getOrPut(book.id) { LocalBookState() }
        state.isAvailable = false
        val request = PendingRequest(requestId = nextRequestId.getAndIncrement().toString(), book = book, memberName = memberName, memberId = memberId, requestDate = "Hari Ini")
        pendingRequests.add(request)
        return true
    }
    fun getPendingRequests(): List<PendingRequest> = pendingRequests.toList()
    fun approveRequest(requestId: String): Boolean {
        val request = pendingRequests.find { it.requestId == requestId } ?: return false
        
        // Update local state for the book
        val state = localBookState.getOrPut(request.book.id) { LocalBookState() }
        
        // 1. Dapatkan tanggal hari ini
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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
                iterator.remove()
                return true
            }
        }
        return false
    }
    fun rejectRequest(requestId: String): Boolean {
        val request = pendingRequests.find { it.requestId == requestId } ?: return false
        // Update local state to mark as available again
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

    // --- Registration Management (UPDATED: Instant Activation) ---

    /**
     * Mendaftarkan anggota baru dan langsung mengaktifkannya (Req: Registrasi -> Langsung Login).
     * Anggota baru memiliki status isVerified = false.
     */
    fun registerNewMember(
        fullName: String,
        nik: String,
        email: String,
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
            val response = ApiConfig.getApiService().getUserById(userId)
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

    // --- Admin Data (Tetap) ---
    fun getTopBooks(): Map<String, Int> { return mapOf("To Kill a Mockingbird" to 45, "1984" to 38, "The Great Gatsby" to 32, "Atomic Habits" to 25, "Pride and Prejudice" to 19) }
    fun findMemberByNik(nik: String): User? { return activeMembers.find { it.nik == nik } }
}