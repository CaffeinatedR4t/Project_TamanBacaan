package com.caffeinatedr4t.tamanbacaan.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.BookAdapter
import com.caffeinatedr4t.tamanbacaan.api.ApiConfig
import com.caffeinatedr4t.tamanbacaan.api.model.BookResponse
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.utils.toBook
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Fragment untuk halaman utama (Home).
 * Menampilkan daftar buku dari backend API.
 */
class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private val booksList = mutableListOf<Book>()
    private var progressBar: ProgressBar? = null
    private var tvError: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupRecyclerView(view)
        loadBooksFromApi()
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewBooks)
        progressBar = view.findViewById(R.id.progressBar)
        tvError = view.findViewById(R.id.tvError)
    }

    private fun setupRecyclerView(view: View) {
        bookAdapter = BookAdapter(booksList) { book ->
            // Click handling is done inside BookAdapter
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
    }

    /**
     * Load books from backend API
     */
    private fun loadBooksFromApi() {
        showLoading(true)
        hideError()

        val apiService = ApiConfig.getApiService()
        apiService.getAllBooks().enqueue(object : Callback<List<BookResponse>> {
            override fun onResponse(
                call: Call<List<BookResponse>>,
                response: Response<List<BookResponse>>
            ) {
                showLoading(false)

                if (response.isSuccessful) {
                    val booksResponse = response.body()
                    if (booksResponse != null) {
                        booksList.clear()
                        // Convert BookResponse to Book using extension function
                        booksList.addAll(booksResponse.map { it.toBook() })
                        bookAdapter.notifyDataSetChanged()

                        if (booksList.isEmpty()) {
                            showError("Tidak ada buku tersedia")
                        }

                        Log.d("HomeFragment", "Loaded ${booksList.size} books from API")
                    } else {
                        showError("Data buku kosong")
                    }
                } else {
                    val errorMsg = "Gagal memuat buku: ${response.code()}"
                    showError(errorMsg)
                    Log.e("HomeFragment", errorMsg)
                    // Fallback to local data if API fails
                    loadBooksFromRepository()
                }
            }

            override fun onFailure(call: Call<List<BookResponse>>, t: Throwable) {
                showLoading(false)
                val errorMsg = "Kesalahan koneksi: ${t.message}"
                showError(errorMsg)
                Log.e("HomeFragment", errorMsg, t)
                
                // Fallback to local data if network fails
                loadBooksFromRepository()
                
                Toast.makeText(
                    context,
                    "Gagal terhubung ke server. Menampilkan data lokal.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    /**
     * Fallback method to load books from local repository
     */
    private fun loadBooksFromRepository() {
        booksList.clear()
        booksList.addAll(BookRepository.getAllBooks())
        bookAdapter.notifyDataSetChanged()
        Log.d("HomeFragment", "Loaded ${booksList.size} books from local repository")
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        tvError?.visibility = View.VISIBLE
        tvError?.text = message
        recyclerView.visibility = View.GONE
    }

    private fun hideError() {
        tvError?.visibility = View.GONE
    }

    /**
     * Public method for SearchFragment to get all books
     */
    internal fun getSampleLibraryBooks(): List<Book> {
        return booksList.ifEmpty {
            BookRepository.getAllBooks()
        }
    }
}