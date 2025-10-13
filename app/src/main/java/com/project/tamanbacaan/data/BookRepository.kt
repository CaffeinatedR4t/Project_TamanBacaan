package com.caffeinatedr4t.tamanbacaan.data

import com.caffeinatedr4t.tamanbacaan.models.Book
import java.util.concurrent.atomic.AtomicLong

/**
 * Repository yang mensimulasikan database/API backend.
 * Data disimpan secara in-memory (hilang saat aplikasi ditutup).
 * Mendukung fungsi CRUD untuk manajemen buku.
 */
object BookRepository {

    private val bookList = mutableListOf<Book>()
    private val nextId = AtomicLong(5) // Mulai ID dari 5 setelah 4 buku sample

    init {
        // Data Sample Awal
        bookList.add(Book(id = "1", title = "To Kill a Mockingbird", author = "Harper Lee", description = "A classic novel about racial injustice in the American South", coverUrl = "", category = "Fiction", isAvailable = true, isbn = "978-0-06-112008-4", avgRating = 4.5f, totalReviews = 120))
        bookList.add(Book(id = "2", title = "1984", author = "George Orwell", description = "A dystopian social science fiction novel", coverUrl = "", category = "Fiction", isAvailable = false, isbn = "978-0-452-28423-4", avgRating = 4.8f, totalReviews = 250))
        bookList.add(Book(id = "3", title = "The Great Gatsby", author = "F. Scott Fitzgerald", description = "The story of Jay Gatsby's pursuit of the American Dream", coverUrl = "", category = "Classic", isAvailable = true, isbn = "978-0-7432-7356-5", avgRating = 4.1f, totalReviews = 90))
        bookList.add(Book(id = "4", title = "Pride and Prejudice", author = "Jane Austen", description = "A romantic novel of manners", coverUrl = "", category = "Romance", isAvailable = true, isbn = "978-0-14-143951-8", avgRating = 4.3f, totalReviews = 150))
    }

    // CREATE / ADD
    fun addBook(newBook: Book): Boolean {
        // Simulasi ID otomatis
        val bookWithId = newBook.copy(id = nextId.getAndIncrement().toString())
        bookList.add(0, bookWithId) // Tambahkan di awal agar terlihat
        return true
    }

    // READ / GET ALL
    fun getAllBooks(): List<Book> = bookList.toList()

    // READ / GET ONE
    fun getBookById(id: String): Book? = bookList.find { it.id == id }

    // UPDATE
    fun updateBook(updatedBook: Book): Boolean {
        val index = bookList.indexOfFirst { it.id == updatedBook.id }
        return if (index != -1) {
            bookList[index] = updatedBook
            true
        } else {
            false
        }
    }

    // DELETE
    fun deleteBook(id: String): Boolean {
        return bookList.removeIf { it.id == id }
    }

    // Data Tambahan untuk Simulasi Admin
    fun getSampleMembers(): List<String> {
        return listOf(
            "M001 - Budi Santoso (RT 005/RW 003)",
            "M002 - Siti Aisyah (RT 004/RW 003)",
            "M003 - Daffa Permana (Anak, Verifikasi Ortu)",
            "M004 - Rina Dewi",
            "M005 - Taufik Hidayat"
        )
    }

    // Data Tambahan untuk Statistik (Buku Terpopuler)
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