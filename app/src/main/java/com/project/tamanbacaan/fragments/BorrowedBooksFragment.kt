package com.caffeinatedr4t.tamanbacaan.fragments

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
import com.caffeinatedr4t.tamanbacaan.adapters.BookAdapter
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.utils.SharedPrefsManager
import kotlinx.coroutines.launch

/**
 * Fragment yang menampilkan daftar buku yang sedang dipinjam atau direquest.
 */
class BorrowedBooksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    // TextView untuk pesan jika tidak ada buku
    private var emptyTextView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_books_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewMyBooks)
        // Jika Anda punya TextView untuk empty state di layout, inisialisasi di sini
        // emptyTextView = view.findViewById(R.id.tvEmptyMyBooks)

        recyclerView.layoutManager = LinearLayoutManager(context)

        loadMyBooks()
    }

    override fun onResume() {
        super.onResume()
        loadMyBooks() // Refresh data saat kembali ke tab ini
    }

    private fun loadMyBooks() {
        lifecycleScope.launch {
            // 1. Cek apakah ID ada di Repository
            var userId = BookRepository.currentUserId

            // 2. [SELF-HEALING] Jika null, coba ambil paksa dari SharedPreferences
            if (userId == null) {
                val prefs = SharedPrefsManager(requireContext())
                val user = prefs.getUser()
                if (user != null && !user.id.isNullOrEmpty()) {
                    userId = user.id
                    BookRepository.setUserId(userId) // Simpan kembali ke repo
                }
            }

            // 3. Eksekusi jika ID valid
            if (userId != null) {
                // Gunakan getAllBooksWithStatus agar status PENDING/BORROWED terbaca
                val allBooks = BookRepository.getAllBooksWithStatus()

                // Filter hanya buku yang PENDING atau BORROWED
                val myBooks = allBooks.filter {
                    it.status == "PENDING" || it.status == "BORROWED"
                }

                bookAdapter = BookAdapter(myBooks) { _ ->
                    // Handle klik item jika perlu
                }
                recyclerView.adapter = bookAdapter

                // Tampilkan pesan/toast jika kosong
                if (myBooks.isEmpty()) {
                    // Jika ingin menggunakan Toast:
                    // Toast.makeText(context, "Belum ada buku yang dipinjam", Toast.LENGTH_SHORT).show()
                }

            } else {
                // Jika benar-benar tidak ada ID (misal belum login), arahkan keluar
                Toast.makeText(context, "Sesi habis, silakan login ulang.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}