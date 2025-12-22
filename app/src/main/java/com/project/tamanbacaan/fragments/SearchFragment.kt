package com.caffeinatedr4t.tamanbacaan.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.activities.BookDetailActivity
import com.caffeinatedr4t.tamanbacaan.adapters.BookAdapter
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.utils.Constants
import com.caffeinatedr4t.tamanbacaan.utils.SharedPrefsManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var etSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptySearch: TextView
    private lateinit var chipGroupCategories: ChipGroup
    private lateinit var bookAdapter: BookAdapter

    private val allBooks = mutableListOf<Book>()
    private val searchResults = mutableListOf<Book>()
    private var selectedCategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSearch = view.findViewById(R.id.etSearch)
        recyclerView = view.findViewById(R.id.recyclerViewSearchResults)
        tvEmptySearch = view.findViewById(R.id.tvEmptySearch)
        chipGroupCategories = view.findViewById(R.id.chipGroupCategories)

        setupRecyclerView()
        setupSearchListener()
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            if (BookRepository.currentUserId == null) {
                val prefs = SharedPrefsManager(requireContext())
                val user = prefs.getUser()
                if (user != null && !user.id.isNullOrEmpty()) {
                    BookRepository.setUserId(user.id)
                }
            }

            val books = BookRepository.getAllBooksWithStatus()

            allBooks.clear()
            allBooks.addAll(books)

            setupCategoryChips()

            if (etSearch.text.toString().isNotEmpty()) {
                filterBooks(etSearch.text.toString())
            }
        }
    }

    private fun setupRecyclerView() {
        // [FIX] Menggunakan Named Arguments
        bookAdapter = BookAdapter(
            books = searchResults,
            onActionClick = { book ->
                // Logic Borrow (Pinjam) dari halaman Search
                lifecycleScope.launch {
                    val userId = BookRepository.currentUserId
                    if (userId != null) {
                        val success = BookRepository.requestBorrowBook(book, userId)
                        if (success) {
                            Toast.makeText(context, "Permintaan pinjam berhasil", Toast.LENGTH_SHORT).show()
                            loadData() // Refresh status button
                        } else {
                            Toast.makeText(context, "Gagal meminjam buku", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onBookmarkClick = { book ->
                // Logic Bookmark
                lifecycleScope.launch {
                    BookRepository.toggleBookmark(book.id)
                    // Tidak perlu reloadData() karena adapter update UI secara optimistic
                }
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
        recyclerView.visibility = View.GONE
    }

    private fun setupCategoryChips() {
        chipGroupCategories.removeAllViews()
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

        val fullSearchList = allBooks.filter { book ->
            val matchesQuery = lowerCaseQuery.isEmpty() ||
                    book.title.lowercase().contains(lowerCaseQuery) ||
                    book.author.lowercase().contains(lowerCaseQuery) ||
                    book.category.lowercase().contains(lowerCaseQuery)

            val matchesCategory = selectedCategory == null || book.category == selectedCategory
            matchesQuery && matchesCategory
        }

        searchResults.addAll(fullSearchList)

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