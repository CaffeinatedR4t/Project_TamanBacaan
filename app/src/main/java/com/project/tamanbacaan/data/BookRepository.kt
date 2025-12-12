package com.caffeinatedr4t.tamanbacaan.data

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

/**
 * Hybrid Repository: Fetches books from MongoDB API while maintaining local state for
 * bookmarks, borrowed status, and pending requests.
 */
object BookRepository {
    // API Service instance
    private val apiService = ApiConfig.getApiService()
    private val eventNotifications = mutableListOf<EventNotification>()
    private val nextEventId = AtomicLong(3) // Lanjutkan dari ID 2
    
    // Local state management for bookmarks and borrowed books
    private val bookmarkedBookIds = mutableSetOf<String>()
    private val borrowedBooksMap = mutableMapOf<String, Pair<String, String>>() // bookId -> (borrowedDate, dueDate)
    
    private val pendingRequests = mutableListOf<PendingRequest>()
    // registrationRequests REMOVED (Diganti dengan aktivasi instan)
    private val activeMembers = mutableListOf<User>()

    private val nextBookId = AtomicLong(6) // Lanjutkan dari ID 5
    private val nextRequestId = AtomicLong(3) // Lanjutkan dari ID 2
    private val nextUserId = AtomicLong(103) // Lanjutkan ID anggota setelah M102

    init {
        // Data Sample untuk event
        eventNotifications.add(EventNotification("1", "Bedah Buku 'Laut Bercerita'", "Ikuti bedah buku bersama penulis Leila S. Chudori pada 10 Oktober 2025!", "05/10/2025"))
        eventNotifications.add(EventNotification("2", "Diskon Sewa Buku 50%", "Nikmati diskon 50% untuk semua kategori buku hingga 12 Oktober 2025!", "07/10/2025"))

        // Data Sample Anggota Aktif Default (isVerified disimulasikan)
        activeMembers.add(User(id="M100", fullName="Budi Santoso", email="user@test.com", nik="32xxxxxxxxxxxxxx", addressRtRw = "RT 005/RW 003, Kel. Demo", isChild = false, parentName = null, isVerified = true)) // Sudah diverifikasi
        activeMembers.add(User(id="M101", fullName="Siti Aisyah", email="siti@test.com", nik="32xxxxxxxxxxxxxy", addressRtRw = "RT 004/RW 003, Kel. Demo", isChild = false, parentName = null, isVerified = false)) // Belum diverifikasi
        activeMembers.add(User(id="M102", fullName="Daffa Permana", email="daffa@test.com", nik="32xxxxxxxxxxxxzz", addressRtRw = "RT 002/RW 001, Kel. Demo", isChild = true, parentName = "Ayah Daffa", isVerified = false)) // Belum diverifikasi
    }

    fun toggleBookmarkStatus(bookId: String): Boolean {
        return if (bookmarkedBookIds.contains(bookId)) {
            bookmarkedBookIds.remove(bookId)
            true
        } else {
            bookmarkedBookIds.add(bookId)
            true
        }
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

    // --- Book CRUD ---
    fun addBook(newBook: Book): Boolean {
        // For now, we can't add to MongoDB from here without proper API endpoint
        // This would need to be implemented on the backend and called via API
        // Placeholder for future implementation
        return true
    }
    
    /**
     * Fetch all books from MongoDB API
     * Applies local state (bookmarks, borrowed status) to the fetched books
     */
    suspend fun getAllBooks(): List<Book> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllBooks()
            if (response.isSuccessful) {
                val books = response.body() ?: emptyList()
                // Apply local state to books
                books.map { book ->
                    val borrowedData = borrowedBooksMap[book.id]
                    book.copy(
                        isBookmarked = bookmarkedBookIds.contains(book.id),
                        isBorrowed = borrowedData != null,
                        borrowedDate = borrowedData?.first,
                        dueDate = borrowedData?.second,
                        isAvailable = borrowedData == null && book.stock > 0
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Fetch a specific book by ID from MongoDB API
     * Applies local state (bookmarks, borrowed status) to the fetched book
     */
    suspend fun getBookById(id: String): Book? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getBookById(id)
            if (response.isSuccessful) {
                val book = response.body()
                book?.let {
                    val borrowedData = borrowedBooksMap[it.id]
                    it.copy(
                        isBookmarked = bookmarkedBookIds.contains(it.id),
                        isBorrowed = borrowedData != null,
                        borrowedDate = borrowedData?.first,
                        dueDate = borrowedData?.second,
                        isAvailable = borrowedData == null && it.stock > 0
                    )
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun updateBook(updatedBook: Book): Boolean {
        // Update local borrowed state
        if (updatedBook.isBorrowed && updatedBook.borrowedDate != null && updatedBook.dueDate != null) {
            borrowedBooksMap[updatedBook.id] = Pair(updatedBook.borrowedDate!!, updatedBook.dueDate!!)
        } else {
            borrowedBooksMap.remove(updatedBook.id)
        }
        
        // Update bookmark state
        if (updatedBook.isBookmarked) {
            bookmarkedBookIds.add(updatedBook.id)
        } else {
            bookmarkedBookIds.remove(updatedBook.id)
        }
        
        return true
    }
    
    fun deleteBook(id: String): Boolean {
        // For now, we can't delete from MongoDB from here without proper API endpoint
        // This would need to be implemented on the backend and called via API
        // Remove from local state
        bookmarkedBookIds.remove(id)
        borrowedBooksMap.remove(id)
        return true
    }

    // --- Transaction Request Management (Tetap) ---
    fun addPendingRequest(book: Book, memberName: String, memberId: String): Boolean {
        if (book.isBorrowed || pendingRequests.any { it.book.id == book.id }) { return false }
        // No longer updating bookList since it comes from API
        val request = PendingRequest(requestId = nextRequestId.getAndIncrement().toString(), book = book, memberName = memberName, memberId = memberId, requestDate = "Hari Ini")
        pendingRequests.add(request)
        return true
    }
    
    fun getPendingRequests(): List<PendingRequest> = pendingRequests.toList()
    
    fun approveRequest(requestId: String): Boolean {
        val request = pendingRequests.find { it.requestId == requestId } ?: return false

        // 1. Dapatkan tanggal hari ini
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val borrowedDate = dateFormat.format(calendar.time)

        // 2. Tambahkan 14 hari untuk jatuh tempo
        calendar.add(Calendar.DAY_OF_YEAR, 14)
        val dueDate = dateFormat.format(calendar.time)

        // 3. Update local borrowed state
        borrowedBooksMap[request.book.id] = Pair(borrowedDate, dueDate)

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
        // No longer updating bookList since it comes from API
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
    fun getAllMembers(): List<User> = activeMembers.toList()

    /**
     * Fungsi yang digunakan Admin untuk memverifikasi status RT/RW (Verifikasi Warga).
     */
    fun toggleVerificationStatus(userId: String): Boolean {
        val index = activeMembers.indexOfFirst { it.id == userId }
        return if (index != -1) {
            val user = activeMembers[index]
            activeMembers[index] = user.copy(isVerified = !user.isVerified)
            true
        } else {
            false
        }
    }

    fun updateMember(updatedUser: User): Boolean {
        val index = activeMembers.indexOfFirst { it.id == updatedUser.id };
        return if (index != -1) { activeMembers[index] = updatedUser; true } else { false }
    }

    fun deleteMember(id: String): Boolean {
        val iterator = activeMembers.iterator();
        while (iterator.hasNext()) { if (iterator.next().id == id) { iterator.remove(); return true } };
        return false
    }

    // --- Admin Data (Tetap) ---
    fun getTopBooks(): Map<String, Int> { return mapOf("To Kill a Mockingbird" to 45, "1984" to 38, "The Great Gatsby" to 32, "Atomic Habits" to 25, "Pride and Prejudice" to 19) }
    fun findMemberByNik(nik: String): User? { return activeMembers.find { it.nik == nik } }
}