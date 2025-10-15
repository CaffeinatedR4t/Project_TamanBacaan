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

class BookmarkedFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
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

        recyclerView = view.findViewById(R.id.recyclerViewMyBooks)
        setupRecyclerView()
        loadBookmarkedBooks()
    }

    // 👈 2. TAMBAHKAN onResume AGAR LIST SELALU UPDATE
    override fun onResume() {
        super.onResume()
        // Muat ulang data setiap kali fragment ini ditampilkan
        loadBookmarkedBooks()
    }

    private fun setupRecyclerView() {
        // Inisialisasi adapter dengan list kosong terlebih dahulu
        bookAdapter = BookAdapter(emptyList()) { /* Klik item ditangani di adapter */ }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
    }

    // 👈 3. BUAT FUNGSI BARU UNTUK MENGAMBIL DATA ASLI
    private fun loadBookmarkedBooks() {
        // Ambil semua buku dari repository, lalu filter yang statusnya isBookmarked = true
        val bookmarkedBooks = BookRepository.getAllBooks().filter { it.isBookmarked }

        // Perbarui adapter dengan data yang sudah difilter
        bookAdapter = BookAdapter(bookmarkedBooks) { }
        recyclerView.adapter = bookAdapter
    }
}