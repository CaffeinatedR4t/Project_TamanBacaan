package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.app.Activity
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
    private lateinit var recyclerView: RecyclerView // RecyclerView untuk menampilkan daftar buku
    private lateinit var bookAdapter: AdminBookAdapter // Adapter untuk daftar buku admin
    private val booksList = mutableListOf<Book>() // Daftar buku yang ditampilkan

    // UI: Form Tambah Buku
    private lateinit var formTitle: TextView // Judul form (Tambah Buku Baru)
    private lateinit var etTitle: EditText // Input Judul Buku
    private lateinit var etAuthor: EditText // Input Penulis
    private lateinit var etCategory: EditText // Input Kategori
    private lateinit var etStock: EditText // Input Stok (Jumlah)
    private lateinit var btnSaveBook: Button // Tombol Simpan Buku

    /**
     * Activity Result Launcher untuk memproses hasil dari EditBookActivity.
     * Digunakan untuk me-refresh daftar buku jika edit berhasil (RESULT_OK).
     */
    private val editBookLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadBooks() // Refresh list setelah edit berhasil
        }
    }

    /**
     * Membuat dan mengembalikan hierarki tampilan fragmen.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_book_management, container, false)
    }

    /**
     * Dipanggil setelah `onCreateView()`.
     * Menginisialisasi semua View, menyiapkan RecyclerView, dan memuat data awal.
     */
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

        setupRecyclerView()
        loadBooks()
        setupFormListeners()
    }

    /**
     * Dipanggil ketika fragment kembali ke latar depan (visible).
     * Memastikan daftar buku selalu diperbarui.
     */
    override fun onResume() {
        super.onResume()
        loadBooks()
    }

    /**
     * Menyiapkan RecyclerView dengan adapter dan callback untuk aksi delete.
     */
    private fun setupRecyclerView() {
        // Menggunakan AdminBookAdapter dengan callback untuk Delete
        bookAdapter = AdminBookAdapter(
            books = booksList,
            onDeleteClick = { book -> showDeleteConfirmation(book) } // Callback saat tombol Delete diklik
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
    }

    /**
     * Memuat daftar semua buku dari BookRepository (API) dan memperbarui adapter.
     */
    private fun loadBooks() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                booksList.clear()
                booksList.addAll(BookRepository.getAllBooks())
                bookAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error loading books: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Menyiapkan listener untuk tombol simpan buku.
     */
    private fun setupFormListeners() {
        btnSaveBook.setOnClickListener {
            saveNewBook()
        }
    }

    /**
     * Menampilkan dialog konfirmasi sebelum menghapus buku.
     * @param book Objek Book yang akan dihapus.
     */
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
     * Mengambil data dari form, memvalidasi, membuat objek Book baru, dan menyimpannya ke repository.
     */
    private fun saveNewBook() {
        val title = etTitle.text.toString().trim()
        val author = etAuthor.text.toString().trim()
        val category = etCategory.text.toString().trim()
        val stock = etStock.text.toString().toIntOrNull() ?: 0 // Mengambil stok, default 0

        // Validasi input
        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(context, "Judul dan Penulis harus diisi.", Toast.LENGTH_SHORT).show()
            return
        }

        if (stock < 0) {
            Toast.makeText(context, "Stok tidak boleh negatif.", Toast.LENGTH_SHORT).show()
            return
        }

        // Membuat objek Book baru (ID akan diisi oleh Repository)
        val newBook = Book(
            id = "",
            title = title,
            author = author,
            category = category.ifEmpty { "Uncategorized" },
            isAvailable = stock > 0, // Ketersediaan ditentukan oleh stok
            description = "Deskripsi buku akan diisi melalui halaman edit.",
            coverUrl = ""
        )

        // Menyimpan buku ke repository
        BookRepository.addBook(newBook)
        Toast.makeText(context, "Buku baru '$title' berhasil ditambahkan!", Toast.LENGTH_SHORT).show()

        // Reset UI dan muat ulang daftar
        loadBooks()
        resetForm()
    }

    /**
     * Mengatur ulang teks pada elemen form ke keadaan awal (untuk input buku baru).
     */
    private fun resetForm() {
        formTitle.text = "Tambah Buku Baru"
        etTitle.text.clear()
        etAuthor.text.clear()
        etCategory.text.clear()
        etStock.text.clear()
        btnSaveBook.text = "SIMPAN BUKU BARU"
    }

    /**
     * Menghapus buku dari repository.
     * @param book Objek Book yang akan dihapus.
     */
    private fun deleteBook(book: Book) {
        if (BookRepository.deleteBook(book.id)) {
            Toast.makeText(context, "'${book.title}' berhasil dihapus.", Toast.LENGTH_SHORT).show()
            loadBooks() // Muat ulang daftar
        } else {
            Toast.makeText(context, "Gagal menghapus buku.", Toast.LENGTH_SHORT).show()
        }
    }
}