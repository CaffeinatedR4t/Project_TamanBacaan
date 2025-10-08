package com.caffeinatedr4t.tamanbacaan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.adapters.BookAdapter

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private val booksList = mutableListOf<Book>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        loadBooks()
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewBooks)
        bookAdapter = BookAdapter(booksList) { book ->
            // Logic navigasi sudah dipindahkan ke BookAdapter untuk kesederhanaan
            // Jika Anda ingin menangani navigasi di sini, hapus logic intent dari adapter.
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
    }

    private fun loadBooks() {
        // Sample books for library
        booksList.clear()
        booksList.addAll(getSampleLibraryBooks())
        bookAdapter.notifyDataSetChanged()
    }

    public fun getSampleLibraryBooks(): List<Book> {
        return listOf(
            Book(
                id = "1",
                title = "To Kill a Mockingbird",
                author = "Harper Lee",
                description = "A classic novel about racial injustice in the American South",
                coverUrl = "",
                category = "Fiction",
                isAvailable = true,
                isbn = "978-0-06-112008-4",
                avgRating = 4.5f, // BARU
                totalReviews = 120 // BARU
            ),
            Book(
                id = "2",
                title = "1984",
                author = "George Orwell",
                description = "A dystopian social science fiction novel",
                coverUrl = "",
                category = "Fiction",
                isAvailable = false,
                isbn = "978-0-452-28423-4",
                avgRating = 4.8f, // BARU
                totalReviews = 250 // BARU
            ),
            Book(
                id = "3",
                title = "The Great Gatsby",
                author = "F. Scott Fitzgerald",
                description = "The story of Jay Gatsby's pursuit of the American Dream",
                coverUrl = "",
                category = "Classic",
                isAvailable = true,
                isbn = "978-0-7432-7356-5",
                avgRating = 4.1f, // BARU
                totalReviews = 90 // BARU
            ),
            Book(
                id = "4",
                title = "Pride and Prejudice",
                author = "Jane Austen",
                description = "A romantic novel of manners",
                coverUrl = "",
                category = "Romance",
                isAvailable = true,
                isbn = "978-0-14-143951-8",
                avgRating = 4.3f, // BARU
                totalReviews = 150 // BARU
            )
        )
    }
}