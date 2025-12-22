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
 * Fragment yang menampilkan daftar buku yang telah ditandai (bookmarked) oleh pengguna.
 * Fragment ini terletak di dalam TabLayout/ViewPager2 dari BookmarkFragment.
 */
class BookmarkedFragment : Fragment() {

    // Variabel untuk RecyclerView yang menampilkan daftar buku
    private lateinit var recyclerView: RecyclerView
    // Adapter untuk mengelola dan menampilkan data buku dalam RecyclerView
    private lateinit var bookAdapter: BookAdapter


    /**
     * Dipanggil untuk membuat dan mengembalikan hierarki tampilan yang terkait dengan fragmen.
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
     * Dipanggil setelah `onCreateView()` dan memastikan view sudah dibuat.
     * Inisialisasi RecyclerView dan memuat data bookmark.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi RecyclerView dari layout
        recyclerView = view.findViewById(R.id.recyclerViewMyBooks)
        setupRecyclerView()
        loadBookmarkedBooks()
    }

    /**
     * Dipanggil ketika fragment mulai terlihat oleh pengguna.
     * Memastikan daftar buku yang ditandai selalu diperbarui (refresh) setiap kali fragment aktif
     * karena status bookmark bisa berubah di fragment lain.
     */
    override fun onResume() {
        super.onResume()
        loadBookmarkedBooks()
    }

    /**
     * Menyiapkan RecyclerView dengan layout manager dan adapter awal.
     */
    private fun setupRecyclerView() {
        // Inisialisasi adapter dengan list kosong terlebih dahulu
        // [UPDATE] Added empty callbacks to satisfy constructor
        bookAdapter = BookAdapter(emptyList(), {}, {})
        recyclerView.apply {
            // Mengatur layout manager ke LinearLayoutManager (daftar vertikal)
            layoutManager = LinearLayoutManager(context)
            // Mengatur adapter ke RecyclerView
            adapter = bookAdapter
        }
    }

    /**
     * Mengambil daftar buku dari repositori, memfilter yang statusnya `isBookmarked = true`,
     * dan memperbarui adapter RecyclerView.
     */
    private fun loadBookmarkedBooks() {
        // Use lifecycleScope to call suspend function
        lifecycleScope.launch {
            // [UPDATE] Gunakan getAllBooksWithStatus agar status bookmark sinkron dengan API/User Profile
            val allBooks = BookRepository.getAllBooksWithStatus()

            // Filter yang statusnya isBookmarked = true
            val bookmarkedBooks = allBooks.filter { it.isBookmarked }

            // Membuat adapter baru dengan data yang sudah difilter dan mengaturnya ke RecyclerView
            bookAdapter = BookAdapter(
                books = bookmarkedBooks,
                onActionClick = { /* Klik action (Borrow) tidak dihandle di tab ini */ },
                onBookmarkClick = { book ->
                    // [BARU] Logic Remove Bookmark
                    lifecycleScope.launch {
                        BookRepository.toggleBookmark(book.id)
                        // Refresh list setelah unbookmark agar item hilang dari layar
                        loadBookmarkedBooks()
                    }
                }
            )
            recyclerView.adapter = bookAdapter
        }
    }
}