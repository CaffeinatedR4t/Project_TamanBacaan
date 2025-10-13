package com.caffeinatedr4t.tamanbacaan.data

import com.caffeinatedr4t.tamanbacaan.models.Book
import java.util.concurrent.atomic.AtomicLong

/**
 * Repository yang mensimulasikan database/API backend.
 * Data disimpan secara in-memory (hilang saat aplikasi ditutup).
 * Mendukung fungsi CRUD untuk manajemen buku dan transaksi.
 */
object BookRepository {

    private val bookList = mutableListOf<Book>()
    private val pendingRequests = mutableListOf<PendingRequest>()
    private val nextBookId = AtomicLong(5)
    private val nextRequestId = AtomicLong(1)

    init {
        // Data Sample Awal
        bookList.add(Book(id = "1", title = "To Kill a Mockingbird", author = "Harper Lee", description = "A classic novel about racial injustice in the American South", coverUrl = "", category = "Fiction", isAvailable = true, isbn = "978-0-06-112008-4", avgRating = 4.5f, totalReviews = 120))
        bookList.add(Book(id = "2", title = "1984", author = "George Orwell", description = "A dystopian social science fiction novel", coverUrl = "", category = "Fiction", isAvailable = false, isbn = "978-0-452-28423-4", avgRating = 4.8f, totalReviews = 250))
        bookList.add(Book(id = "3", title = "The Great Gatsby", author = "F. Scott Fitzgerald", description = "The story of Jay Gatsby's pursuit of the American Dream", coverUrl = "", category = "Classic", isAvailable = true, isbn = "978-0-7432-7356-5", avgRating = 4.1f, totalReviews = 90))
        bookList.add(Book(id = "4", title = "Pride and Prejudice", author = "Jane Austen", description = "A romantic novel of manners", coverUrl = "", category = "Romance", isAvailable = true, isbn = "978-0-14-143951-8", avgRating = 4.3f, totalReviews = 150))

        // Data Transaksi Pinjaman Langsung (Simulasi buku sedang dipinjam)
        bookList.add(Book(id = "5", title = "Atomic Habits", author = "James Clear", description = "Tiny changes, remarkable results.", coverUrl = "", category = "Self-Help", isBorrowed = true, isAvailable = false, borrowedDate = "01/10/2025", dueDate = "15/10/2025", avgRating = 4.7f, totalReviews = 300))

        // Data Sample Pending Request
        pendingRequests.add(PendingRequest(requestId = "1", book = bookList.first { it.id == "1" }.copy(isAvailable = false), memberName = "Budi Santoso", memberId = "M001", requestDate = "13/10/2025"))
        pendingRequests.add(PendingRequest(requestId = "2", book = bookList.first { it.id == "3" }.copy(isAvailable = true), memberName = "Siti Aisyah", memberId = "M002", requestDate = "13/10/2025"))
    }

    // --- Book CRUD ---
    fun addBook(newBook: Book): Boolean {
        val bookWithId = newBook.copy(id = nextBookId.getAndIncrement().toString())
        bookList.add(0, bookWithId)
        return true
    }

    fun getAllBooks(): List<Book> = bookList.toList()

    fun getBookById(id: String): Book? = bookList.find { it.id == id }

    fun updateBook(updatedBook: Book): Boolean {
        val index = bookList.indexOfFirst { it.id == updatedBook.id }
        return if (index != -1) {
            bookList[index] = updatedBook
            true
        } else {
            false
        }
    }

    fun deleteBook(id: String): Boolean {
        return bookList.removeIf { it.id == id }
    }

    // --- Transaction Request Management (NEW) ---
    fun addPendingRequest(book: Book, memberName: String, memberId: String): Boolean {
        // Cek jika buku sudah dipinjam atau di-request
        if (book.isBorrowed || pendingRequests.any { it.book.id == book.id }) {
            return false
        }

        // Simulasi ubah status buku menjadi tidak tersedia (jika stok tinggal 1) saat di-request
        val bookIndex = bookList.indexOfFirst { it.id == book.id }
        if (bookIndex != -1) {
            bookList[bookIndex] = bookList[bookIndex].copy(isAvailable = false)
        }

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

        // 1. Ubah status buku menjadi dipinjam (bukan hanya tidak tersedia)
        val bookIndex = bookList.indexOfFirst { it.id == request.book.id }
        if (bookIndex != -1) {
            // Tandai buku sebagai dipinjam oleh seseorang
            val approvedBook = bookList[bookIndex].copy(isAvailable = false, isBorrowed = true)
            bookList[bookIndex] = approvedBook
        }

        // 2. Hapus request dari daftar pending
        pendingRequests.removeIf { it.requestId == requestId }
        return true
    }

    fun rejectRequest(requestId: String): Boolean {
        val request = pendingRequests.find { it.requestId == requestId } ?: return false

        // 1. Kembalikan status buku menjadi tersedia (jika Admin menolak)
        val bookIndex = bookList.indexOfFirst { it.id == request.book.id }
        if (bookIndex != -1) {
            bookList[bookIndex] = bookList[bookIndex].copy(isAvailable = true)
        }

        // 2. Hapus request
        return pendingRequests.removeIf { it.requestId == requestId }
    }


    // --- Admin Data (Tetap) ---
    fun getSampleMembers(): List<String> {
        return listOf(
            "M001 - Budi Santoso (RT 005/RW 003)",
            "M002 - Siti Aisyah (RT 004/RW 003)",
            "M003 - Daffa Permana (Anak, Verifikasi Ortu)",
            "M004 - Rina Dewi",
            "M005 - Taufik Hidayat"
        )
    }

    fun getTopBooks(): Map<String, Int> {
        return mapOf(
            "To Kill a Mockingbird" to 45,
            "1984" to 38,
            "The Great Gatsby" to 32,
            "Atomic Habits" to 25,
            "Pride and Prejudice" to 19
        )
    }
}