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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Fragment untuk menampilkan Laporan dan Statistik (Reports) TBM bagi Admin.
 */
class ReportFragment : Fragment() {

    // 1. [UBAH] Gunakan nama variabel tvReportDate
    private lateinit var tvReportDate: TextView

    private lateinit var tvTotalMembers: TextView
    private lateinit var tvPendingRequests: TextView
    private lateinit var tvBorrowedBooks: TextView
    private lateinit var tvTotalBooks: TextView
    private lateinit var containerTopBooks: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init Container Grafik
        containerTopBooks = view.findViewById(R.id.containerTopBooks)

        // 2. [UBAH] Hubungkan dengan ID R.id.tvReportDate yang ada di XML
        tvReportDate = view.findViewById(R.id.tvReportDate)

        // Init TextViews Statistik
        tvTotalMembers = view.findViewById(R.id.tvTotalMembers)
        tvPendingRequests = view.findViewById(R.id.tvPendingRequests)
        tvBorrowedBooks = view.findViewById(R.id.tvBorrowedBooks)
        tvTotalBooks = view.findViewById(R.id.tvTotalBooks)

        // Set Tanggal Laporan
        displayReportDate()

        // Muat semua data statistik
        loadActivityStats()
    }

    private fun displayReportDate() {
        try {
            // Format: "Desember 2025"
            val dateFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
            val currentMonthYear = dateFormat.format(Date())

            // Set text ke TextView tanggal
            tvReportDate.text = "Bulan: $currentMonthYear"

        } catch (e: Exception) {
            e.printStackTrace()
            tvReportDate.text = "Laporan Bulanan"
        }
    }

    private fun loadActivityStats() {
        lifecycleScope.launch {
            // 1. Ambil semua data yang diperlukan
            val allMembers = BookRepository.getAllMembers()
            val pendingRequests = BookRepository.fetchPendingRequests()
            val borrowedBooksCount = BookRepository.getBorrowedBooksCount()
            val allBooks = BookRepository.getAllBooks()

            // 2. Update UI Statistik Atas
            if (isAdded) {
                tvTotalMembers.text = allMembers.size.toString()
                tvPendingRequests.text = pendingRequests.size.toString()
                tvBorrowedBooks.text = borrowedBooksCount.toString()
                tvTotalBooks.text = allBooks.size.toString()

                // 3. Update Grafik Buku Terpopuler (Top 5)
                val topBooks = allBooks
                    .sortedWith(compareByDescending<Book> { it.totalReviews }
                        .thenByDescending { it.avgRating })
                    .take(5)

                displayTopBooks(topBooks)
            }
        }
    }

    private fun displayTopBooks(books: List<Book>) {
        containerTopBooks.removeAllViews()

        if (books.isEmpty()) {
            val emptyText = TextView(context).apply {
                text = "Belum ada data ulasan buku."
                setTextColor(Color.GRAY)
                setPadding(0, 16, 0, 0)
            }
            containerTopBooks.addView(emptyText)
            return
        }

        val maxMetric = books.firstOrNull()?.totalReviews?.toFloat()?.takeIf { it > 0 } ?: 1f

        books.forEach { book ->
            val barContainer = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 16 }
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            // Judul Buku
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

            // Bar Grafik
            val reviewCount = book.totalReviews
            val barWeight = (reviewCount / maxMetric) * 0.45f
            val finalWeight = if (barWeight < 0.01f) 0.01f else barWeight

            val barView = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    24,
                    finalWeight
                ).apply { marginEnd = 8 }

                setBackgroundColor(
                    if (reviewCount > 0) resources.getColor(R.color.primary_blue)
                    else Color.LTGRAY
                )
            }
            barContainer.addView(barView)

            // Info Angka
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