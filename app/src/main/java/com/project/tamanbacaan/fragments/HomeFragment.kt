package com.caffeinatedr4t.tamanbacaan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.BookAdapter
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.viewmodels.BookViewModel
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import kotlinx.coroutines.launch

/**
 * Fragment untuk halaman utama (Home).
 * Menampilkan daftar buku rekomendasi dalam RecyclerView.
 */
class HomeFragment : Fragment() {

    // RecyclerView untuk menampilkan daftar buku
    private lateinit var recyclerView: RecyclerView
    // Adapter untuk mengelola dan menampilkan data buku
    private lateinit var bookAdapter: BookAdapter
    // MutableList untuk menampung data buku yang akan ditampilkan
    private val booksList = mutableListOf<Book>()
    // ViewModel untuk mengambil data buku dari API
    private lateinit var bookViewModel: BookViewModel

    /**
     * Membuat dan mengembalikan hierarki tampilan fragmen.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    /**
     * Dipanggil setelah `onCreateView()`.
     * Menyiapkan RecyclerView dan memuat daftar buku.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        setupViewModel()
        loadBooks()
    }

    /**
     * Menyiapkan RecyclerView dengan layout manager dan adapter.
     * @param view Tampilan root fragmen.
     */
    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewBooks)

        // [PENTING] Isi lambda ini! Jangan dikosongkan.
        bookAdapter = BookAdapter(booksList) { book ->
            // Saat tombol diklik, panggil ViewModel
            if (book.status == "BORROWED") {
                // Logic Return (bisa ditambahkan di ViewModel nanti)
                Toast.makeText(context, "Fitur kembalikan ada di tab Pinjaman", Toast.LENGTH_SHORT).show()
            } else {
                // Logic Request Pinjam
                bookViewModel.requestBorrow(book)
            }
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
    }

    /**
     * Menyiapkan ViewModel dan observers untuk mengambil data buku dari API.
     */
    private fun setupViewModel() {
        bookViewModel = ViewModelProvider(this)[BookViewModel::class.java]

        bookViewModel.books.observe(viewLifecycleOwner) { books ->
            booksList.clear()
            booksList.addAll(books)
            bookAdapter.notifyDataSetChanged()
        }

        bookViewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        // Observe errors
        bookViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, "Error loading books: $it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Memuat semua data buku dari API melalui ViewModel.
     */
    private fun loadBooks() {
        bookViewModel.fetchBooks()
    }

    /**
     * Mengambil daftar semua buku dari BookRepository.
     * Fungsi ini digunakan oleh SearchFragment untuk mendapatkan data sumber yang lengkap.
     * @return List<Book> Daftar semua buku di perpustakaan.
     */
    internal suspend fun getSampleLibraryBooks(): List<Book> {
        return BookRepository.getAllBooks()
    }

    fun getBooks(): List<Book> {
        lifecycleScope.launch {
            booksList.clear()
            booksList.addAll(getSampleLibraryBooks())
            bookAdapter.notifyDataSetChanged()
        }
        return booksList
    }
}