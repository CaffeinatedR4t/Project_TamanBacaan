package com.caffeinatedr4t.tamanbacaan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.BookAdapter
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.data.BookRepository // Import Repository

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
     * Memuat semua data buku dari BookRepository dan memperbarui RecyclerView.
     */
    private fun loadBooks() {
        booksList.clear()
        // Menggunakan BookRepository untuk mengambil semua data buku yang ada
        booksList.addAll(BookRepository.getAllBooks())
        // Beri tahu adapter bahwa data telah berubah
        bookAdapter.notifyDataSetChanged()
    }

    /**
     * Mengambil daftar semua buku dari BookRepository.
     * Fungsi ini digunakan oleh SearchFragment untuk mendapatkan data sumber yang lengkap.
     * @return List<Book> Daftar semua buku di perpustakaan.
     */
    internal fun getSampleLibraryBooks(): List<Book> {
        return BookRepository.getAllBooks()
    }
}