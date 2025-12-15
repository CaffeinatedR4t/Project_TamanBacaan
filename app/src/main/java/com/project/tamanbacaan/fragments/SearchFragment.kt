package com.caffeinatedr4t.tamanbacaan.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.BookAdapter
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

/**
 * Fragment untuk halaman Pencarian (Search).
 * Memungkinkan pengguna mencari buku berdasarkan judul, penulis, atau kategori,
 * serta memfilter hasil berdasarkan kategori menggunakan ChipGroup.
 */
class SearchFragment : Fragment() {

    // Elemen UI untuk input dan hasil
    private lateinit var etSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptySearch: TextView
    private lateinit var chipGroupCategories: ChipGroup
    private lateinit var bookAdapter: BookAdapter

    // Daftar semua buku yang tersedia
    private val allBooks = mutableListOf<Book>()
    // Daftar hasil pencarian yang akan ditampilkan
    private val searchResults = mutableListOf<Book>()
    // Kategori yang sedang dipilih untuk filter
    private var selectedCategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi View
        etSearch = view.findViewById(R.id.etSearch)
        recyclerView = view.findViewById(R.id.recyclerViewSearchResults)
        tvEmptySearch = view.findViewById(R.id.tvEmptySearch)
        chipGroupCategories = view.findViewById(R.id.chipGroupCategories)

        setupRecyclerView()
        loadBooksAndSetupUI()
    }

    /**
     * Load books from API and setup UI
     */
    private fun loadBooksAndSetupUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val books = BookRepository.getAllBooks()
                allBooks.clear()
                allBooks.addAll(books)
                setupCategoryChips()
                setupSearchListener()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(searchResults) { book ->
            // Logika klik item (sudah ditangani di dalam BookAdapter)
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
        recyclerView.visibility = View.GONE
    }

    private fun setupCategoryChips() {
        // Ambil daftar kategori unik
        val categories = allBooks.map { it.category }.distinct().toMutableList()
        categories.add(0, "Semua")

        categories.forEach { categoryName ->
            val chip = Chip(context).apply {
                text = categoryName
                isCheckable = true
                if (categoryName == "Semua") isChecked = true

                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedCategory = if (categoryName == "Semua") null else categoryName
                        filterBooks(etSearch.text.toString())
                    }
                }
            }
            chipGroupCategories.addView(chip)
        }
    }

    private fun setupSearchListener() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBooks(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterBooks(query: String) {
        searchResults.clear()
        val lowerCaseQuery = query.trim().lowercase()

        // Filter buku berdasarkan kueri dan kategori
        val fullSearchList = allBooks.filter { book ->
            val matchesQuery = lowerCaseQuery.isEmpty() ||
                    book.title.lowercase().contains(lowerCaseQuery) ||
                    book.author.lowercase().contains(lowerCaseQuery) ||
                    book.category.lowercase().contains(lowerCaseQuery)

            val matchesCategory = selectedCategory == null || book.category == selectedCategory

            matchesQuery && matchesCategory
        }

        searchResults.addAll(fullSearchList)

        // Update UI
        if (lowerCaseQuery.isEmpty() && selectedCategory == null) {
            tvEmptySearch.text = "Ketik judul, penulis, atau kategori untuk mencari."
            tvEmptySearch.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else if (fullSearchList.isEmpty()) {
            tvEmptySearch.text = "Tidak ada hasil ditemukan."
            tvEmptySearch.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmptySearch.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        bookAdapter.notifyDataSetChanged()
    }
}