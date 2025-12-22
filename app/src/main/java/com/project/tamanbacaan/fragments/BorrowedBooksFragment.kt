package com.caffeinatedr4t.tamanbacaan.fragments

import android.app.AlertDialog // Pastikan import ini ada
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
    // private var emptyTextView: TextView? = null // Aktifkan jika ada di layout

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
            // 1. Pastikan User ID tersedia (Self-Healing)
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
                // 2. Ambil data buku dengan status terbaru
                val allBooks = BookRepository.getAllBooksWithStatus()

                // 3. Filter hanya buku PENDING atau BORROWED
                val myBooks = allBooks.filter {
                    it.status == "PENDING" || it.status == "BORROWED"
                }

                // 4. Setup Adapter dengan Named Arguments
                bookAdapter = BookAdapter(
                    books = myBooks,
                    onActionClick = { book ->
                        // LOGIKA TOMBOL ACTION (Kembalikan / Detail)
                        if (book.status == "BORROWED") {
                            // Tampilkan dialog konfirmasi pengembalian
                            showReturnConfirmation(book.id, book.title)
                        } else {
                            // Jika PENDING atau lainnya, buka detail atau toast
                            val intent = Intent(context, BookDetailActivity::class.java)
                            intent.putExtra(Constants.EXTRA_BOOK_ID, book.id)
                            startActivity(intent)
                        }
                    },
                    onBookmarkClick = { book ->
                        // LOGIKA BOOKMARK
                        lifecycleScope.launch {
                            BookRepository.toggleBookmarkStatus(book.id)
                            // Tidak perlu refresh full, UI adapter sudah optimistic update
                        }
                    }
                )

                recyclerView.adapter = bookAdapter

                // Tampilkan pesan kosong jika perlu (Opsional)
                if (myBooks.isEmpty()) {
                    Toast.makeText(context, "Tidak ada buku yang sedang dipinjam", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(context, "Sesi habis, silakan login ulang.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Menampilkan dialog konfirmasi "Ya/Tidak" sebelum mengembalikan buku
     */
    private fun showReturnConfirmation(bookId: String, bookTitle: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Kembalikan Buku")
            .setMessage("Apakah Anda yakin ingin mengembalikan buku \"$bookTitle\"?")
            .setPositiveButton("Ya") { _, _ ->
                processReturnBook(bookId)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    /**
     * Proses pengembalian ke backend via Repository
     */
    private fun processReturnBook(bookId: String) {
        lifecycleScope.launch {
            Toast.makeText(context, "Memproses pengembalian...", Toast.LENGTH_SHORT).show()

            // Pastikan Anda sudah menambahkan fungsi returnActiveBook di BookRepository (dari jawaban sebelumnya)
            val isSuccess = BookRepository.returnActiveBook(bookId)

            if (isSuccess) {
                Toast.makeText(context, "Buku berhasil dikembalikan!", Toast.LENGTH_LONG).show()
                loadMyBooks() // Refresh list agar buku hilang dari daftar
            } else {
                Toast.makeText(context, "Gagal mengembalikan buku. Coba lagi.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}