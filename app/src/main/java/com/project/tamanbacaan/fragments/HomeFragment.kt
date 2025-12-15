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
        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewBooks)
        // Inisialisasi adapter dengan daftar buku kosong
        bookAdapter = BookAdapter(booksList) { book ->
            // Logika klik buku (dibiarkan kosong karena sudah ditangani di dalam BookAdapter)
        }

        recyclerView.apply {
            // Mengatur layout manager ke LinearLayoutManager (daftar vertikal)
            layoutManager = LinearLayoutManager(context)
            // Mengatur adapter ke RecyclerView
            adapter = bookAdapter
        }
    }

    /**
     * Menyiapkan ViewModel dan observers untuk mengambil data buku dari API.
     */
    private fun setupViewModel() {
        bookViewModel = ViewModelProvider(this)[BookViewModel::class.java]

        // Observe books data
        bookViewModel.books.observe(viewLifecycleOwner) { books ->
            booksList.clear()
            booksList.addAll(books)
            bookAdapter.notifyDataSetChanged()
        }

        // Observe loading state (optional - could show progress bar)
        bookViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Could show/hide progress bar here
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