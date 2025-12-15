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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_books_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi RecyclerView dari layout
        recyclerView = view.findViewById(R.id.recyclerViewMyBooks)
        setupRecyclerView()
        loadBookmarkedBooks()
    }

    override fun onResume() {
        super.onResume()
        loadBookmarkedBooks()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(emptyList()) { /* Klik item ditangani di adapter */ }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
    }

    /**
     * Load books from API and filter bookmarked books (client-side state)
     */
    private fun loadBookmarkedBooks() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val allBooks = BookRepository.getAllBooks()
                val bookmarkedBooks = allBooks.filter { it.isBookmarked }
                bookAdapter = BookAdapter(bookmarkedBooks) { }
                recyclerView.adapter = bookAdapter
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}