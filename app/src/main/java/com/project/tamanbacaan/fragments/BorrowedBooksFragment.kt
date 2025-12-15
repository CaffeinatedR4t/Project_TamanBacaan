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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_books_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewMyBooks)
        setupRecyclerView()
        loadBorrowedBooks()
    }

    override fun onResume() {
        super.onResume()
        loadBorrowedBooks()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(emptyList()) { /* Klik item ditangani di adapter */ }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
    }

    /**
     * Load books from API and filter borrowed books (client-side state)
     */
    private fun loadBorrowedBooks() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val allBooks = BookRepository.getAllBooks()
                val borrowedBooks = allBooks.filter { it.isBorrowed }
                bookAdapter = BookAdapter(borrowedBooks) { }
                recyclerView.adapter = bookAdapter
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}