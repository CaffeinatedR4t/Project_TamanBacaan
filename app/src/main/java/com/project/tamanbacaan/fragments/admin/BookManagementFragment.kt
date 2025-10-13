package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.BookAdapter
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.models.Book

class BookManagementFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private val booksList = mutableListOf<Book>()

    // Form Views
    private lateinit var formTitle: TextView
    private lateinit var etTitle: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etCategory: EditText
    private lateinit var etStock: EditText
    private lateinit var btnSaveBook: Button

    private var bookToEdit: Book? = null // Untuk menyimpan buku yang sedang diedit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_book_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Form Views
        formTitle = view.findViewById(R.id.formTitle)
        etTitle = view.findViewById(R.id.etTitle)
        etAuthor = view.findViewById(R.id.etAuthor)
        etCategory = view.findViewById(R.id.etCategory)
        etStock = view.findViewById(R.id.etStock)
        btnSaveBook = view.findViewById(R.id.btnSaveBook)
        recyclerView = view.findViewById(R.id.recyclerViewAdminBooks)

        setupRecyclerView()
        loadBooks()
        setupFormListeners()
    }

    private fun setupRecyclerView() {
        // Menggunakan BookAdapter yang sama, tetapi dengan fungsi klik Admin
        bookAdapter = BookAdapter(booksList) { book ->
            // Ketika buku diklik di Admin, mulai mode edit
            startEditMode(book)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
    }

    private fun loadBooks() {
        booksList.clear()
        booksList.addAll(BookRepository.getAllBooks())
        bookAdapter.notifyDataSetChanged()
    }

    private fun setupFormListeners() {
        btnSaveBook.setOnClickListener {
            saveBook()
        }
        // Admin bisa klik item_book untuk mengedit
    }

    private fun startEditMode(book: Book) {
        bookToEdit = book
        formTitle.text = "Edit Buku: ${book.title}"
        etTitle.setText(book.title)
        etAuthor.setText(book.author)
        etCategory.setText(book.category)
        // Simulasi Stok: Ambil isAvailable (true/false) menjadi Stok 1/0 untuk demo CRUD
        etStock.setText(if (book.isAvailable) "1" else "0")
        btnSaveBook.text = "SIMPAN PERUBAHAN"
    }

    private fun saveBook() {
        val title = etTitle.text.toString().trim()
        val author = etAuthor.text.toString().trim()
        val category = etCategory.text.toString().trim()
        val stock = etStock.text.toString().toIntOrNull() ?: 0

        if (title.isEmpty() || author.isEmpty() || stock < 0) {
            Toast.makeText(context, "Judul, Penulis, dan Stok harus valid.", Toast.LENGTH_SHORT).show()
            return
        }

        if (bookToEdit != null) {
            // UPDATE Logic
            val updatedBook = bookToEdit!!.copy(
                title = title,
                author = author,
                category = category,
                isAvailable = stock > 0,
                description = bookToEdit!!.description // Pertahankan deskripsi lama
            )
            BookRepository.updateBook(updatedBook)
            Toast.makeText(context, "Buku berhasil diupdate!", Toast.LENGTH_SHORT).show()
        } else {
            // CREATE Logic
            val newBook = Book(
                id = "", // ID akan diisi oleh Repository
                title = title,
                author = author,
                category = category,
                isAvailable = stock > 0,
                description = "Deskripsi default dari Admin.",
                coverUrl = ""
            )
            BookRepository.addBook(newBook)
            Toast.makeText(context, "Buku baru berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
        }

        // Reset UI
        loadBooks()
        resetForm()
    }

    private fun resetForm() {
        bookToEdit = null
        formTitle.text = "Tambah Buku Baru"
        etTitle.text.clear()
        etAuthor.text.clear()
        etCategory.text.clear()
        etStock.text.clear()
        btnSaveBook.text = "SIMPAN BUKU BARU"
    }

    // Fungsionalitas Delete
    // (Dalam implementasi nyata, tombol Delete harus ada di item RecyclerView Admin)
    fun deleteBook(book: Book) {
        if (BookRepository.deleteBook(book.id)) {
            Toast.makeText(context, "'${book.title}' berhasil dihapus.", Toast.LENGTH_SHORT).show()
            loadBooks()
        } else {
            Toast.makeText(context, "Gagal menghapus buku.", Toast.LENGTH_SHORT).show()
        }
    }
}