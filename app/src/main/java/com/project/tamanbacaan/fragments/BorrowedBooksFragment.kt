package com.caffeinatedr4t.tamanbacaan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
 * Fragment yang menampilkan daftar buku yang sedang dipinjam (Borrowed Books) oleh pengguna.
 * Fragment ini merupakan salah satu tab di dalam BookmarkFragment.
 */
class BorrowedBooksFragment : Fragment() {

    // RecyclerView untuk menampilkan daftar buku pinjaman
    private lateinit var recyclerView: RecyclerView
    // Adapter untuk mengelola dan menampilkan data buku
    private lateinit var bookAdapter: BookAdapter

    // Simulasi data buku yang sedang dipinjam (Data awal untuk keperluan inisialisasi/testing)
    private val borrowedBooks = listOf(
        Book(
            id = "5",
            title = "Atomic Habits",
            author = "James Clear",
            description = "Tiny changes, remarkable results.",
            coverUrl = "",
            category = "Self-Help",
            isBorrowed = true,
            isAvailable = false,
            borrowedDate = "01/10/2025",
            dueDate = "15/10/2025"
        )
    )

    /**
     * Membuat dan mengembalikan hierarki tampilan yang terkait dengan fragmen.
     * Menggunakan layout `fragment_my_books_list` yang berisi RecyclerView.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_books_list, container, false)
    }

    /**
     * Dipanggil setelah `onCreateView()`.
     * Inisialisasi RecyclerView, adapter, dan set initial data/adapter.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewMyBooks)

        // Inisialisasi adapter dengan data dummy/initial
        bookAdapter = BookAdapter(borrowedBooks) { book ->
            // Logika klik item (misalnya, menampilkan detail dengan pengingat pengembalian)
        }

        recyclerView.apply {
            // Mengatur layout manager ke LinearLayoutManager (daftar vertikal)
            layoutManager = LinearLayoutManager(context)
            // Mengatur adapter ke RecyclerView
            adapter = bookAdapter
        }

        // Memastikan RecyclerView disiapkan dan data yang sebenarnya dimuat dari Repository
        setupRecyclerView()
        loadBorrowedBooks()
    }

    /**
     * Dipanggil ketika fragment mulai terlihat oleh pengguna.
     * Memastikan daftar buku pinjaman selalu diperbarui (refresh) dari Repository.
     */
    override fun onResume() {
        super.onResume()
        // Muat ulang data setiap kali fragment ini ditampilkan
        loadBorrowedBooks()
    }

    /**
     * Menyiapkan RecyclerView dengan layout manager dan menginisialisasi adapter dengan list kosong.
     */
    private fun setupRecyclerView() {
        // Inisialisasi ulang adapter dengan list kosong terlebih dahulu
        bookAdapter = BookAdapter(emptyList()) { /* Klik item ditangani di adapter */ }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
    }

    /**
     * Mengambil daftar buku dari BookRepository, memfilter yang statusnya `isBorrowed = true`,
     * dan memperbarui adapter RecyclerView.
     */
    private fun loadBorrowedBooks() {
        // Use lifecycleScope to call suspend function
        lifecycleScope.launch {
            // Mengambil semua buku dari repository, lalu memfilter yang sedang dipinjam
            val borrowedBooks = BookRepository.getAllBooks().filter { it.isBorrowed }

            // Membuat ulang dan mengatur adapter dengan data pinjaman yang sudah difilter
            bookAdapter = BookAdapter(borrowedBooks) { }
            recyclerView.adapter = bookAdapter
        }
    }
}