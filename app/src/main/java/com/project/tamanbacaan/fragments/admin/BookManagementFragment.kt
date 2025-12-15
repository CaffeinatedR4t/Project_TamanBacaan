package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.AdminBookAdapter
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.models.Book
import kotlinx.coroutines.launch

/**
 * Fragment untuk manajemen Buku (CRUD) oleh Admin.
 * Menyediakan form untuk menambah buku baru dan daftar buku untuk diedit/dihapus.
 */
class BookManagementFragment : Fragment() {

    // UI: Daftar Buku
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: AdminBookAdapter
    private val booksList = mutableListOf<Book>()

    // UI: Form Tambah Buku
    private lateinit var formTitle: TextView
    private lateinit var etTitle: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etCategory: EditText
    private lateinit var etStock: EditText
    private lateinit var btnSaveBook: Button
    private var progressBar: ProgressBar? = null

    /**
     * Activity Result Launcher untuk memproses hasil dari EditBookActivity.
     */
    private val editBookLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadBooks() // Refresh list setelah edit berhasil
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_book_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi Form Views
        formTitle = view.findViewById(R.id.formTitle)
        etTitle = view.findViewById(R.id.etTitle)
        etAuthor = view.findViewById(R.id.etAuthor)
        etCategory = view.findViewById(R.id.etCategory)
        etStock = view.findViewById(R.id.etStock)
        btnSaveBook = view.findViewById(R.id.btnSaveBook)
        recyclerView = view.findViewById(R.id.recyclerViewAdminBooks)
        progressBar = view.findViewById(R.id.progressBar)

        setupRecyclerView()
        loadBooks()
        setupFormListeners()
    }

    override fun onResume() {
        super.onResume()
        loadBooks()
    }

    private fun setupRecyclerView() {
        bookAdapter = AdminBookAdapter(
            books = booksList,
            onDeleteClick = { book -> showDeleteConfirmation(book) }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
    }

    /**
     * Load books from API via Repository
     */
    private fun loadBooks() {
        viewLifecycleOwner.lifecycleScope.launch {
            progressBar?.visibility = View.VISIBLE
            try {
                val books = BookRepository.getAllBooks()
                booksList.clear()
                booksList.addAll(books)
                bookAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading books: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar?.visibility = View.GONE
            }
        }
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

    /**
     * Save new book via API
     */
    private fun saveNewBook() {
        val title = etTitle.text.toString().trim()
        val author = etAuthor.text.toString().trim()
        val category = etCategory.text.toString().trim()
        val stock = etStock.text.toString().toIntOrNull() ?: 0

        // Validasi input
        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(context, "Judul dan Penulis harus diisi.", Toast.LENGTH_SHORT).show()
            return
        }

        if (stock < 0) {
            Toast.makeText(context, "Stok tidak boleh negatif.", Toast.LENGTH_SHORT).show()
            return
        }

        // Create new Book object
        val newBook = Book(
            id = "", // Will be generated by backend
            title = title,
            author = author,
            category = category.ifEmpty { "Uncategorized" },
            isAvailable = stock > 0,
            description = "Deskripsi buku akan diisi melalui halaman edit.",
            coverUrl = "",
            stock = stock,
            totalCopies = stock
        )

        // Save via API
        viewLifecycleOwner.lifecycleScope.launch {
            progressBar?.visibility = View.VISIBLE
            try {
                val success = BookRepository.addBook(newBook)
                if (success) {
                    Toast.makeText(context, "Buku baru '$title' berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    loadBooks()
                    resetForm()
                } else {
                    Toast.makeText(context, "Gagal menambahkan buku. Silakan coba lagi.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar?.visibility = View.GONE
            }
        }
    }

    private fun resetForm() {
        formTitle.text = "Tambah Buku Baru"
        etTitle.text.clear()
        etAuthor.text.clear()
        etCategory.text.clear()
        etStock.text.clear()
        btnSaveBook.text = "SIMPAN BUKU BARU"
    }

    /**
     * Delete book via API
     */
    private fun deleteBook(book: Book) {
        viewLifecycleOwner.lifecycleScope.launch {
            progressBar?.visibility = View.VISIBLE
            try {
                val success = BookRepository.deleteBook(book.id)
                if (success) {
                    Toast.makeText(context, "'${book.title}' berhasil dihapus.", Toast.LENGTH_SHORT).show()
                    loadBooks()
                } else {
                    Toast.makeText(context, "Gagal menghapus buku.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar?.visibility = View.GONE
            }
        }
    }
}