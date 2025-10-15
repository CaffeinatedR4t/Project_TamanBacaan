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
import com.caffeinatedr4t.tamanbacaan.data.BookRepository

class BorrowedBooksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter

    // Simulasi data buku yang sedang dipinjam
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_books_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewMyBooks)
        bookAdapter = BookAdapter(borrowedBooks) { book ->
            // Handle click (e.g. show detail with return reminder)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        // Muat ulang data setiap kali fragment ini ditampilkan
        loadBorrowedBooks()
    }

    private fun setupRecyclerView() {
        // Inisialisasi adapter dengan list kosong terlebih dahulu
        bookAdapter = BookAdapter(emptyList()) { /* Klik item ditangani di adapter */ }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
    }

    private fun loadBorrowedBooks() {
        // Ambil semua buku dari repository, lalu filter yang statusnya isBorrowed = true
        val borrowedBooks = BookRepository.getAllBooks().filter { it.isBorrowed }

        // Perbarui adapter dengan data yang sudah difilter
        bookAdapter = BookAdapter(borrowedBooks) { }
        recyclerView.adapter = bookAdapter
    }
}