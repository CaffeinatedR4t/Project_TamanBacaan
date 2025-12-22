package com.caffeinatedr4t.tamanbacaan.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.caffeinatedr4t.tamanbacaan.utils.Constants
import com.caffeinatedr4t.tamanbacaan.utils.SharedPrefsManager
import kotlinx.coroutines.launch

/**
 * Fragment yang menampilkan daftar buku yang sedang dipinjam atau direquest.
 */
class BorrowedBooksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private var emptyTextView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_books_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewMyBooks)
        recyclerView.layoutManager = LinearLayoutManager(context)

        loadMyBooks()
    }

    override fun onResume() {
        super.onResume()
        loadMyBooks() // Refresh data saat kembali ke tab ini
    }

    private fun loadMyBooks() {
        lifecycleScope.launch {
            var userId = BookRepository.currentUserId

            if (userId == null) {
                val prefs = SharedPrefsManager(requireContext())
                val user = prefs.getUser()
                if (user != null && !user.id.isNullOrEmpty()) {
                    userId = user.id
                    BookRepository.setUserId(userId)
                }
            }

            if (userId != null) {
                val allBooks = BookRepository.getAllBooksWithStatus()

                // Filter hanya buku yang PENDING atau BORROWED
                val myBooks = allBooks.filter {
                    it.status == "PENDING" || it.status == "BORROWED"
                }

                // [FIX] Menggunakan Named Arguments untuk mengatasi error parameter
                bookAdapter = BookAdapter(
                    books = myBooks,
                    onActionClick = { book ->
                        // Saat klik tombol action (Kembalikan/Detail)
                        val intent = Intent(context, BookDetailActivity::class.java)
                        intent.putExtra(Constants.EXTRA_BOOK_ID, book.id)
                        startActivity(intent)
                    },
                    onBookmarkClick = { book ->
                        // Logic Bookmark
                        lifecycleScope.launch {
                            BookRepository.toggleBookmark(book.id)
                        }
                    }
                )
                recyclerView.adapter = bookAdapter

                if (myBooks.isEmpty()) {
                    // Optional: Show empty state
                }

            } else {
                Toast.makeText(context, "Sesi habis, silakan login ulang.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}