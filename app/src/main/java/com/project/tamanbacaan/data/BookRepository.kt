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
 * Repository that uses API calls to fetch book data from MongoDB backend.
 * Local state is maintained for client-side features (bookmarks, borrowed status).
 */
object BookRepository {
    private val eventNotifications = mutableListOf<EventNotification>()
    private val nextEventId = AtomicLong(3)
    
    // Local cache for books with client-side state overlay
    private val bookList = mutableListOf<Book>()
    private val bookmarkIds = mutableSetOf<String>() // Track bookmarked books
    private val borrowedBookIds = mutableSetOf<String>() // Track borrowed books
    
    private val pendingRequests = mutableListOf<PendingRequest>()
    private val activeMembers = mutableListOf<User>()

    private val nextRequestId = AtomicLong(3)
    private val nextUserId = AtomicLong(103)

    init {
        // Data Sample untuk event
        eventNotifications.add(EventNotification("1", "Bedah Buku 'Laut Bercerita'", "Ikuti bedah buku bersama penulis Leila S. Chudori pada 10 Oktober 2025!", "05/10/2025"))
        eventNotifications.add(EventNotification("2", "Diskon Sewa Buku 50%", "Nikmati diskon 50% untuk semua kategori buku hingga 12 Oktober 2025!", "07/10/2025"))

        // Data Anggota Aktif Default
        activeMembers.add(User(id="M100", fullName="Budi Santoso", email="user@test.com", nik="32xxxxxxxxxxxxxx", addressRtRw = "RT 005/RW 003, Kel. Demo", isChild = false, parentName = null, isVerified = true))
        activeMembers.add(User(id="M101", fullName="Siti Aisyah", email="siti@test.com", nik="32xxxxxxxxxxxxxy", addressRtRw = "RT 004/RW 003, Kel. Demo", isChild = false, parentName = null, isVerified = false))
        activeMembers.add(User(id="M102", fullName="Daffa Permana", email="daffa@test.com", nik="32xxxxxxxxxxxxzz", addressRtRw = "RT 002/RW 001, Kel. Demo", isChild = true, parentName = "Ayah Daffa", isVerified = false))
    }

    /**
     * Apply local client-side state to a book
     */
    private fun applyLocalState(book: Book): Book {
        return book.copy(
            isBookmarked = bookmarkIds.contains(book.id),
            isBorrowed = borrowedBookIds.contains(book.id)
        )
    }

    fun toggleBookmarkStatus(bookId: String): Boolean {
        if (bookmarkIds.contains(bookId)) {
            bookmarkIds.remove(bookId)
        } else {
            bookmarkIds.add(bookId)
        }
        // Update local cache
        val bookIndex = bookList.indexOfFirst { it.id == bookId }
        if (bookIndex != -1) {
            val book = bookList[bookIndex]
            bookList[bookIndex] = book.copy(isBookmarked = !book.isBookmarked)
        }
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

    // --- Book CRUD via API ---
    
    /**
     * Fetch all books from MongoDB API and apply local client state
     */
    suspend fun getAllBooks(): List<Book> {
        return try {
            val response = ApiConfig.getApiService().getAllBooks()
            if (response.isSuccessful) {
                val books = response.body() ?: emptyList()
                bookList.clear()
                bookList.addAll(books.map { applyLocalState(it) })
                bookList.toList()
            } else {
                // Return cached data if API fails
                bookList.toList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Return cached data if API call fails
            bookList.toList()
        }
    }

    /**
     * Get book by ID from API
     */
    suspend fun getBookById(id: String): Book? {
        return try {
            val response = ApiConfig.getApiService().getBookById(id)
            if (response.isSuccessful) {
                response.body()?.let { applyLocalState(it) }
            } else {
                // Fallback to cache
                bookList.find { it.id == id }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            bookList.find { it.id == id }
        }
    }

    /**
     * Add new book via API
     */
    suspend fun addBook(newBook: Book): Boolean {
        return try {
            val response = ApiConfig.getApiService().createBook(newBook)
            if (response.isSuccessful) {
                response.body()?.let { createdBook ->
                    bookList.add(0, applyLocalState(createdBook))
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Update book via API
     */
    suspend fun updateBook(updatedBook: Book): Boolean {
        return try {
            val response = ApiConfig.getApiService().updateBook(updatedBook.id, updatedBook)
            if (response.isSuccessful) {
                val index = bookList.indexOfFirst { it.id == updatedBook.id }
                if (index != -1) {
                    bookList[index] = applyLocalState(updatedBook)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Delete book via API
     */
    suspend fun deleteBook(id: String): Boolean {
        return try {
            val response = ApiConfig.getApiService().deleteBook(id)
            if (response.isSuccessful) {
                bookList.removeAll { it.id == id }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // --- Transaction Request Management (Local State) ---
    fun addPendingRequest(book: Book, memberName: String, memberId: String): Boolean {
        if (book.isBorrowed || pendingRequests.any { it.book.id == book.id }) { return false }
        val bookIndex = bookList.indexOfFirst { it.id == book.id }
        if (bookIndex != -1) { 
            bookList[bookIndex] = bookList[bookIndex].copy(isAvailable = false) 
        }
        val request = PendingRequest(requestId = nextRequestId.getAndIncrement().toString(), book = book, memberName = memberName, memberId = memberId, requestDate = "Hari Ini")
        pendingRequests.add(request)
        return true
    }
    
    fun getPendingRequests(): List<PendingRequest> = pendingRequests.toList()
    
    fun approveRequest(requestId: String): Boolean {
        val request = pendingRequests.find { it.requestId == requestId } ?: return false
        val bookIndex = bookList.indexOfFirst { it.id == request.book.id }

        if (bookIndex != -1) {
            // 1. Get current date
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val borrowedDate = dateFormat.format(calendar.time)

            // 2. Add 14 days for due date
            calendar.add(Calendar.DAY_OF_YEAR, 14)
            val dueDate = dateFormat.format(calendar.time)

            // 3. Update book with dates
            val approvedBook = bookList[bookIndex].copy(
                isAvailable = false,
                isBorrowed = true,
                borrowedDate = borrowedDate,
                dueDate = dueDate
            )
            bookList[bookIndex] = approvedBook
            borrowedBookIds.add(approvedBook.id)
        }

        // Remove from pending requests
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
        val bookIndex = bookList.indexOfFirst { it.id == request.book.id }
        if (bookIndex != -1) { 
            bookList[bookIndex] = bookList[bookIndex].copy(isAvailable = true) 
        }
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