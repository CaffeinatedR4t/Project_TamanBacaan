package com.caffeinatedr4t.tamanbacaan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.BookAdapter
import com.caffeinatedr4t.tamanbacaan.models.Book
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
    // Loading indicator
    private var progressBar: ProgressBar? = null

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

        progressBar = view.findViewById(R.id.progressBar)
        setupRecyclerView(view)
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
     * Memuat semua data buku dari API via BookRepository dan memperbarui RecyclerView.
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

    /**
     * Mengambil daftar semua buku dari BookRepository.
     * Fungsi ini digunakan oleh SearchFragment untuk mendapatkan data sumber yang lengkap.
     * @return List<Book> Daftar semua buku di perpustakaan.
     */
    internal fun getSampleLibraryBooks(): List<Book> {
        return booksList.toList()
    }
}