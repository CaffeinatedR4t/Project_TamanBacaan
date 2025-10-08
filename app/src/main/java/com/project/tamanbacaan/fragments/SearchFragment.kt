package com.caffeinatedr4t.tamanbacaan.fragments // KOREKSI PACKAGE

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
import com.caffeinatedr4t.tamanbacaan.fragments.HomeFragment
// Import untuk mendapatkan daftar buku sample

class SearchFragment : Fragment() {

    private lateinit var etSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptySearch: TextView
    private lateinit var bookAdapter: BookAdapter

    // Gunakan daftar buku dari HomeFragment sebagai sumber data simulasi
    private val allBooks = HomeFragment().getSampleLibraryBooks()
    private val searchResults = mutableListOf<Book>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Layout sudah diperbarui di Fix 2
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSearch = view.findViewById(R.id.etSearch)
        recyclerView = view.findViewById(R.id.recyclerViewSearchResults)
        tvEmptySearch = view.findViewById(R.id.tvEmptySearch)

        setupRecyclerView()
        setupSearchListener()
    }

    private fun setupRecyclerView() {
        // Gunakan BookAdapter yang sama untuk menampilkan hasil
        bookAdapter = BookAdapter(searchResults) { book ->
            // Handle book click - navigate to book details (Logic sudah di handle di adapter)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }

        // Mulai dengan menyembunyikan recycler view
        recyclerView.visibility = View.GONE
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

        if (lowerCaseQuery.isEmpty()) {
            // Tampilkan pesan kosong/instruksi saat tidak ada input
            tvEmptySearch.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            // Filter berdasarkan judul, penulis, atau kategori (Req. 3)
            val filteredList = allBooks.filter {
                it.title.lowercase().contains(lowerCaseQuery) ||
                        it.author.lowercase().contains(lowerCaseQuery) ||
                        it.category.lowercase().contains(lowerCaseQuery)
            }

            searchResults.addAll(filteredList)

            if (filteredList.isEmpty()) {
                tvEmptySearch.text = "Tidak ada hasil ditemukan untuk \"$query\"."
                tvEmptySearch.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                tvEmptySearch.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
        bookAdapter.notifyDataSetChanged()
    }
}