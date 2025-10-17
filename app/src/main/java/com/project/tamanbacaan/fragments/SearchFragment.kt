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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.BookAdapter
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 * Fragment untuk halaman Pencarian (Search).
 * Memungkinkan pengguna mencari buku berdasarkan judul, penulis, atau kategori,
 * serta memfilter hasil berdasarkan kategori menggunakan ChipGroup.
 */
class SearchFragment : Fragment() {

    // Elemen UI untuk input dan hasil
    private lateinit var etSearch: EditText // Input teks pencarian
    private lateinit var recyclerView: RecyclerView // RecyclerView untuk menampilkan hasil
    private lateinit var tvEmptySearch: TextView // TextView untuk pesan empty state/instruksi
    private lateinit var chipGroupCategories: ChipGroup // Grup Chip untuk filter kategori
    private lateinit var bookAdapter: BookAdapter // Adapter untuk RecyclerView

    // Daftar semua buku yang tersedia (diambil dari HomeFragment/Repository)
    private val allBooks = HomeFragment().getSampleLibraryBooks()
    // Daftar hasil pencarian yang akan ditampilkan
    private val searchResults = mutableListOf<Book>()
    // Kategori yang sedang dipilih untuk filter. Null jika "Semua" dipilih.
    private var selectedCategory: String? = null

    /**
     * Membuat dan mengembalikan hierarki tampilan fragmen.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    /**
     * Dipanggil setelah `onCreateView()`.
     * Menginisialisasi UI dan menyiapkan listener.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi View
        etSearch = view.findViewById(R.id.etSearch)
        recyclerView = view.findViewById(R.id.recyclerViewSearchResults)
        tvEmptySearch = view.findViewById(R.id.tvEmptySearch)
        chipGroupCategories = view.findViewById(R.id.chipGroupCategories)

        setupRecyclerView()
        setupCategoryChips()
        setupSearchListener()
    }

    /**
     * Menyiapkan RecyclerView untuk menampilkan hasil pencarian.
     */
    private fun setupRecyclerView() {
        // Inisialisasi adapter dengan list hasil pencarian
        bookAdapter = BookAdapter(searchResults) { book ->
            // Logika klik item (sudah ditangani di dalam BookAdapter)
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
        // Sembunyikan RecyclerView secara default
        recyclerView.visibility = View.GONE
    }

    /**
     * Membuat dan menambahkan Chip untuk setiap kategori unik yang tersedia.
     */
    private fun setupCategoryChips() {
        // Ambil daftar kategori unik, lalu tambahkan "Semua" di awal
        val categories = allBooks.map { it.category }.distinct().toMutableList()
        categories.add(0, "Semua")

        categories.forEach { categoryName ->
            val chip = Chip(context).apply {
                text = categoryName
                isCheckable = true
                if (categoryName == "Semua") isChecked = true

                // Listener ketika status check chip berubah
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        // Atur kategori yang dipilih. Jika "Semua", set null.
                        selectedCategory = if (categoryName == "Semua") null else categoryName
                        filterBooks(etSearch.text.toString()) // Jalankan ulang filter
                    }
                }
            }
            chipGroupCategories.addView(chip)
        }
    }

    /**
     * Menyiapkan listener untuk EditText pencarian agar melakukan filter real-time.
     */
    private fun setupSearchListener() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            // Dipanggil setiap kali teks berubah, memicu filter
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBooks(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /**
     * Melakukan filter pada daftar buku berdasarkan kueri (query) pencarian dan kategori yang dipilih.
     * Memperbarui RecyclerView dan status tampilan kosong (empty state).
     * @param query Teks pencarian yang dimasukkan oleh pengguna.
     */
    private fun filterBooks(query: String) {
        searchResults.clear()
        val lowerCaseQuery = query.trim().lowercase()

        // Filter buku berdasarkan kueri dan kategori
        val fullSearchList = allBooks.filter { book ->
            // Kriteria pencarian: Judul, penulis, atau kategori mengandung kueri
            val matchesQuery = lowerCaseQuery.isEmpty() ||
                    book.title.lowercase().contains(lowerCaseQuery) ||
                    book.author.lowercase().contains(lowerCaseQuery) ||
                    book.category.lowercase().contains(lowerCaseQuery)

            // Kriteria kategori: Kategori buku harus sama dengan yang dipilih (jika ada)
            val matchesCategory = selectedCategory == null || book.category == selectedCategory

            matchesQuery && matchesCategory // Gabungkan kriteria
        }

        searchResults.addAll(fullSearchList)

        // Mengatur tampilan empty state dan RecyclerView
        if (lowerCaseQuery.isEmpty() && selectedCategory == null) {
            // Tampilan default saat tidak ada input pencarian
            tvEmptySearch.text = "Ketik judul, penulis, atau kategori untuk mencari."
            tvEmptySearch.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else if (fullSearchList.isEmpty()) {
            // Tampilan saat hasil pencarian kosong
            tvEmptySearch.text = "Tidak ada hasil ditemukan."
            tvEmptySearch.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            // Tampilan saat ada hasil
            tvEmptySearch.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        // Beri tahu adapter bahwa data telah berubah
        bookAdapter.notifyDataSetChanged()
    }
}