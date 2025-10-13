package com.caffeinatedr4t.tamanbacaan.data

import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.models.User
import java.util.concurrent.atomic.AtomicLong

object BookRepository {

    private val bookList = mutableListOf<Book>()
    private val pendingRequests = mutableListOf<PendingRequest>()
    private val registrationRequests = mutableListOf<RegistrationRequest>()
    private val activeMembers = mutableListOf<User>()

    private val nextBookId = AtomicLong(5)
    private val nextRequestId = AtomicLong(1)
    private val nextRegId = AtomicLong(3) // Lanjutkan ID Registrasi setelah 2 sample
    private val nextUserId = AtomicLong(103) // Lanjutkan ID anggota setelah M102

    init {
        // Data Sample Buku (Tetap)
        bookList.add(Book(id = "1", title = "To Kill a Mockingbird", author = "Harper Lee", description = "A classic novel about racial injustice in the American South", coverUrl = "", category = "Fiction", isAvailable = true, isbn = "978-0-06-112008-4", avgRating = 4.5f, totalReviews = 120))
        bookList.add(Book(id = "2", title = "1984", author = "George Orwell", description = "A dystopian social science fiction novel", coverUrl = "", category = "Fiction", isAvailable = false, isbn = "978-0-452-28423-4", avgRating = 4.8f, totalReviews = 250))
        bookList.add(Book(id = "3", title = "The Great Gatsby", author = "F. Scott Fitzgerald", description = "The story of Jay Gatsby's pursuit of the American Dream", coverUrl = "", category = "Classic", isAvailable = true, isbn = "978-0-7432-7356-5", avgRating = 4.1f, totalReviews = 90))
        bookList.add(Book(id = "4", title = "Pride and Prejudice", author = "Jane Austen", description = "A romantic novel of manners", coverUrl = "", category = "Romance", isAvailable = true, isbn = "978-0-14-143951-8", avgRating = 4.3f, totalReviews = 150))
        bookList.add(Book(id = "5", title = "Atomic Habits", author = "James Clear", description = "Tiny changes, remarkable results.", coverUrl = "", category = "Self-Help", isBorrowed = true, isAvailable = false, borrowedDate = "01/10/2025", dueDate = "15/10/2025", avgRating = 4.7f, totalReviews = 300))

        // Data Sample Pending Request Pinjaman (Tetap)
        pendingRequests.add(PendingRequest(requestId = "1", book = bookList.first { it.id == "1" }.copy(isAvailable = false), memberName = "Budi Santoso", memberId = "M001", requestDate = "13/10/2025"))
        pendingRequests.add(PendingRequest(requestId = "2", book = bookList.first { it.id == "3" }.copy(isAvailable = true), memberName = "Siti Aisyah", memberId = "M002", requestDate = "13/10/2025"))

        // Data Sample Pending Request Registrasi (Tetap)
        registrationRequests.add(RegistrationRequest(requestId = "1", fullName = "Daffa Permana", nik = "3277xxxxxxxxxxxx", isChild = true, parentName = "Ayah Daffa", addressRtRw = "RT 002/RW 001, Kel. UMN", requestDate = "Hari Ini"))
        registrationRequests.add(RegistrationRequest(requestId = "2", fullName = "Rina Dewi", nik = "3277xxxxxxxxxxxx", isChild = false, parentName = null, addressRtRw = "RT 005/RW 003, Kel. UMN", requestDate = "Kemarin"))

        // Data Anggota Aktif Default (Tetap)
        activeMembers.add(User(id="M100", fullName="Budi Santoso", email="budi@test.com", nik="32xxxxxxxxxxxxxx", addressRtRw = "RT 005/RW 003, Kel. Demo", isChild = false, parentName = null))
        activeMembers.add(User(id="M101", fullName="Siti Aisyah", email="siti@test.com", nik="32xxxxxxxxxxxxxy", addressRtRw = "RT 004/RW 003, Kel. Demo", isChild = false, parentName = null))
        activeMembers.add(User(id="M102", fullName="Daffa Permana", email="daffa@test.com", nik="32xxxxxxxxxxxxzz", addressRtRw = "RT 002/RW 001, Kel. Demo", isChild = true, parentName = "Ayah Daffa"))
    }

    // --- Book CRUD (Tetap) ---
    fun addBook(newBook: Book): Boolean { /* ... */ val bookWithId = newBook.copy(id = nextBookId.getAndIncrement().toString()); bookList.add(0, bookWithId); return true }
    fun getAllBooks(): List<Book> = bookList.toList()
    fun getBookById(id: String): Book? = bookList.find { it.id == id }
    fun updateBook(updatedBook: Book): Boolean { val index = bookList.indexOfFirst { it.id == updatedBook.id }; return if (index != -1) { bookList[index] = updatedBook; true } else { false } }
    fun deleteBook(id: String): Boolean { val iterator = bookList.iterator(); while (iterator.hasNext()) { if (iterator.next().id == id) { iterator.remove(); return true } }; return false }

    // --- Transaction Request Management (Tetap) ---
    fun addPendingRequest(book: Book, memberName: String, memberId: String): Boolean {
        if (book.isBorrowed || pendingRequests.any { it.book.id == book.id }) { return false }
        val bookIndex = bookList.indexOfFirst { it.id == book.id }
        if (bookIndex != -1) { bookList[bookIndex] = bookList[bookIndex].copy(isAvailable = false) }
        val request = PendingRequest(requestId = nextRequestId.getAndIncrement().toString(), book = book, memberName = memberName, memberId = memberId, requestDate = "Hari Ini")
        pendingRequests.add(request)
        return true
    }
    fun getPendingRequests(): List<PendingRequest> = pendingRequests.toList()
    fun approveRequest(requestId: String): Boolean { val request = pendingRequests.find { it.requestId == requestId } ?: return false; val bookIndex = bookList.indexOfFirst { it.id == request.book.id }; if (bookIndex != -1) { val approvedBook = bookList[bookIndex].copy(isAvailable = false, isBorrowed = true); bookList[bookIndex] = approvedBook }; val iterator = pendingRequests.iterator(); while (iterator.hasNext()) { if (iterator.next().requestId == requestId) { iterator.remove(); return true } }; return false }
    fun rejectRequest(requestId: String): Boolean { val request = pendingRequests.find { it.requestId == requestId } ?: return false; val bookIndex = bookList.indexOfFirst { it.id == request.book.id }; if (bookIndex != -1) { bookList[bookIndex] = bookList[bookIndex].copy(isAvailable = true) }; val iterator = pendingRequests.iterator(); while (iterator.hasNext()) { if (iterator.next().requestId == requestId) { iterator.remove(); return true } }; return false }

    // --- Registration Request Management ---

    // BARU: Fungsi untuk menerima request dari halaman Registrasi (Client)
    fun addRegistrationRequest(request: RegistrationRequest): Boolean {
        // Asumsi email belum terdaftar
        val requestWithId = request.copy(requestId = nextRegId.getAndIncrement().toString())
        registrationRequests.add(requestWithId)
        return true
    }

    fun getRegistrationRequests(): List<RegistrationRequest> = registrationRequests.toList()

    fun approveRegistration(requestId: String): Boolean {
        val request = registrationRequests.find { it.requestId == requestId } ?: return false

        // 1. Tambahkan ke daftar Anggota Aktif
        val newUser = User(
            id = "M${nextUserId.getAndIncrement()}",
            fullName = request.fullName,
            email = "user_${nextUserId.get() - 1}@test.com",
            nik = request.nik,
            addressRtRw = request.addressRtRw,
            isChild = request.isChild,
            parentName = request.parentName
        )
        activeMembers.add(newUser)

        // 2. Hapus request dari daftar pending
        val iterator = registrationRequests.iterator()
        var success = false
        while (iterator.hasNext()) {
            if (iterator.next().requestId == requestId) {
                iterator.remove()
                success = true
            }
        }
        return success
    }

    fun rejectRegistration(requestId: String): Boolean {
        val iterator = registrationRequests.iterator()
        var success = false
        while (iterator.hasNext()) {
            if (iterator.next().requestId == requestId) {
                iterator.remove()
                success = true
            }
        }
        return success
    }

    // --- Member Management (Tetap) ---
    fun getAllMembers(): List<User> = activeMembers.toList()
    fun updateMember(updatedUser: User): Boolean { val index = activeMembers.indexOfFirst { it.id == updatedUser.id }; return if (index != -1) { activeMembers[index] = updatedUser; true } else { false } }
    fun deleteMember(id: String): Boolean { val iterator = activeMembers.iterator(); while (iterator.hasNext()) { if (iterator.next().id == id) { iterator.remove(); return true } }; return false }
    fun getTopBooks(): Map<String, Int> { return mapOf("To Kill a Mockingbird" to 45, "1984" to 38, "The Great Gatsby" to 32, "Atomic Habits" to 25, "Pride and Prejudice" to 19) }
}