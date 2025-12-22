package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.models.Book
import kotlinx.coroutines.launch

/**
 * Fragment untuk menampilkan Laporan dan Statistik (Reports) TBM bagi Admin.
 */
class ReportFragment : Fragment() {

    private lateinit var tvTotalMembers: TextView
    private lateinit var tvPendingRequests: TextView
    private lateinit var tvBorrowedBooks: TextView
    private lateinit var tvTotalBooks: TextView
    private lateinit var containerTopBooks: LinearLayout // Container untuk grafik

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init Container Grafik
        containerTopBooks = view.findViewById(R.id.containerTopBooks)

        // Init TextViews Statistik
        tvTotalMembers = view.findViewById(R.id.tvTotalMembers)
        tvPendingRequests = view.findViewById(R.id.tvPendingRequests)
        tvBorrowedBooks = view.findViewById(R.id.tvBorrowedBooks)
        tvTotalBooks = view.findViewById(R.id.tvTotalBooks)

        // Muat semua data
        loadActivityStats()
    }

    private fun loadActivityStats() {
        lifecycleScope.launch {
            // 1. Ambil semua data yang diperlukan
            val allMembers = BookRepository.getAllMembers()
            val pendingRequests = BookRepository.fetchPendingRequests() // Gunakan fetch live dari API
            val borrowedBooksCount = BookRepository.getBorrowedBooksCount()
            val allBooks = BookRepository.getAllBooks()

            // 2. Update UI Statistik Atas
            if (isAdded) {
                tvTotalMembers.text = allMembers.size.toString()
                tvPendingRequests.text = pendingRequests.size.toString()
                tvBorrowedBooks.text = borrowedBooksCount.toString()
                tvTotalBooks.text = allBooks.size.toString()

                // 3. Update Grafik Buku Terpopuler (Top 5)
                // Logika: Urutkan berdasarkan Total Reviews terbanyak, lalu Avg Rating tertinggi
                val topBooks = allBooks
                    .sortedWith(compareByDescending<Book> { it.totalReviews }
                        .thenByDescending { it.avgRating })
                    .take(5)

                displayTopBooks(topBooks)
            }
        }
    }

    /**
     * Menampilkan visualisasi bar chart berdasarkan data buku asli.
     */
    private fun displayTopBooks(books: List<Book>) {
        containerTopBooks.removeAllViews() // Bersihkan view lama

        if (books.isEmpty()) {
            val emptyText = TextView(context).apply {
                text = "Belum ada data ulasan buku."
                setTextColor(Color.GRAY)
                setPadding(0, 16, 0, 0)
            }
            containerTopBooks.addView(emptyText)
            return
        }

        // Tentukan nilai maksimum untuk skala bar (gunakan totalReviews tertinggi)
        // Tambahkan safety check agar tidak membagi dengan 0
        val maxMetric = books.firstOrNull()?.totalReviews?.toFloat()?.takeIf { it > 0 } ?: 1f

        books.forEach { book ->
            // Container per baris
            val barContainer = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 16 } // Beri jarak antar baris
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            // 1. Judul Buku (35% lebar)
            val titleView = TextView(context).apply {
                text = book.title
                maxLines = 1
                ellipsize = android.text.TextUtils.TruncateAt.END
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.35f
                ).apply { marginEnd = 8 }
                setTextColor(Color.parseColor("#212121"))
                textSize = 12f
            }
            barContainer.addView(titleView)

            // 2. Bar Visualisasi (Flexible width)
            // Hitung persentase lebar berdasarkan totalReviews
            val reviewCount = book.totalReviews
            val barWeight = (reviewCount / maxMetric) * 0.45f // Max 45% lebar layar

            // Minimal bar tetap terlihat meski review sedikit (0.01f)
            val finalWeight = if (barWeight < 0.01f) 0.01f else barWeight

            val barView = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    24, // Tinggi bar
                    finalWeight
                ).apply { marginEnd = 8 }

                // Warna bar beda dikit jika review 0
                setBackgroundColor(
                    if (reviewCount > 0) resources.getColor(R.color.primary_blue)
                    else Color.LTGRAY
                )
            }
            barContainer.addView(barView)

            // 3. Info Statistik (Rating & Review)
            // Format: "4.5★ (10)"
            val statsText = "${book.avgRating}★ (${book.totalReviews})"

            val statsView = TextView(context).apply {
                text = statsText
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setTextColor(resources.getColor(R.color.primary_blue_dark))
                textSize = 12f
                setTypeface(null, Typeface.BOLD)
            }
            barContainer.addView(statsView)

            containerTopBooks.addView(barContainer)
        }
    }
}