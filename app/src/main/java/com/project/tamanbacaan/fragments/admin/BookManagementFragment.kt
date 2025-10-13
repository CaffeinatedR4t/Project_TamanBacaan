package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.activities.EditBookActivity
import com.caffeinatedr4t.tamanbacaan.adapters.AdminBookAdapter
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.utils.Constants

class BookManagementFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: AdminBookAdapter
    private val booksList = mutableListOf<Book>()

    // Form Views
    private lateinit var formTitle: TextView
    private lateinit var etTitle: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etCategory: EditText
    private lateinit var etStock: EditText
    private lateinit var btnSaveBook: Button

    // Activity Result Launcher untuk refresh data setelah edit
    private val editBookLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadBooks() // Refresh list setelah edit
        }
    }

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

    override fun onResume() {
        super.onResume()
        // Refresh list ketika kembali ke fragment ini
        loadBooks()
    }

    private fun setupRecyclerView() {
        // Menggunakan AdminBookAdapter dengan callback untuk Delete saja
        bookAdapter = AdminBookAdapter(
            books = booksList,
            onDeleteClick = { book -> showDeleteConfirmation(book) }
        )

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
            saveNewBook()
        }
    }

    private fun showDeleteConfirmation(book: Book) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Buku")
            .setMessage("Apakah Anda yakin ingin menghapus '${book.title}'?\n\nData buku akan dihapus permanen.")
            .setPositiveButton("Hapus") { _, _ ->
                deleteBook(book)
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun saveNewBook() {
        val title = etTitle.text.toString().trim()
        val author = etAuthor.text.toString().trim()
        val category = etCategory.text.toString().trim()
        val stock = etStock.text.toString().toIntOrNull() ?: 0

        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(context, "Judul dan Penulis harus diisi.", Toast.LENGTH_SHORT).show()
            return
        }

        if (stock < 0) {
            Toast.makeText(context, "Stok tidak boleh negatif.", Toast.LENGTH_SHORT).show()
            return
        }

        // CREATE Logic
        val newBook = Book(
            id = "", // ID akan diisi oleh Repository
            title = title,
            author = author,
            category = category.ifEmpty { "Uncategorized" },
            isAvailable = stock > 0,
            description = "Deskripsi buku akan diisi melalui halaman edit.",
            coverUrl = ""
        )

        BookRepository.addBook(newBook)
        Toast.makeText(context, "Buku baru '$title' berhasil ditambahkan!", Toast.LENGTH_SHORT).show()

        // Reset UI
        loadBooks()
        resetForm()
    }

    private fun resetForm() {
        formTitle.text = "Tambah Buku Baru"
        etTitle.text.clear()
        etAuthor.text.clear()
        etCategory.text.clear()
        etStock.text.clear()
        btnSaveBook.text = "SIMPAN BUKU BARU"
    }

    private fun deleteBook(book: Book) {
        if (BookRepository.deleteBook(book.id)) {
            Toast.makeText(context, "'${book.title}' berhasil dihapus.", Toast.LENGTH_SHORT).show()
            loadBooks()
        } else {
            Toast.makeText(context, "Gagal menghapus buku.", Toast.LENGTH_SHORT).show()
        }
    }
}