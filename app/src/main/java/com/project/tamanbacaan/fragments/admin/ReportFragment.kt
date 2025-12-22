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
import kotlinx.coroutines.launch

/**
 * Fragment untuk menampilkan Laporan dan Statistik (Reports) TBM bagi Admin.
 * Menampilkan statistik kunci seperti jumlah anggota, permintaan pinjaman, buku dipinjam, dan buku terpopuler.
 */
class ReportFragment : Fragment() {

    // TextViews untuk statistik aktivitas TBM
    private lateinit var tvTotalMembers: TextView // Jumlah total anggota aktif
    private lateinit var tvPendingRequests: TextView // Jumlah permintaan pinjaman yang tertunda
    private lateinit var tvBorrowedBooks: TextView // Jumlah total buku yang sedang dipinjam
    private lateinit var tvTotalBooks: TextView // Jumlah total koleksi buku


    /**
     * Membuat dan mengembalikan hierarki tampilan fragmen.
     * Menggunakan layout `fragment_admin_report`.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_report, container, false)
    }

    /**
     * Dipanggil setelah `onCreateView()`.
     * Menginisialisasi View dan memuat data statistik.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi container untuk Top Books
        val container = view.findViewById<LinearLayout>(R.id.containerTopBooks)
        // Menampilkan visualisasi buku terpopuler
        displayTopBooks(container)

        // Inisialisasi TextViews untuk statistik TBM
        tvTotalMembers = view.findViewById(R.id.tvTotalMembers)
        tvPendingRequests = view.findViewById(R.id.tvPendingRequests)
        tvBorrowedBooks = view.findViewById(R.id.tvBorrowedBooks)
        tvTotalBooks = view.findViewById(R.id.tvTotalBooks)

        // Muat data statistik aktivitas
        loadActivityStats()
    }

    /**
     * Mengambil data statistik aktivitas TBM (Anggota, Pinjaman, Request) dari BookRepository dan menampilkannya di UI.
     */
    private fun loadActivityStats() {
        lifecycleScope.launch {
            // 1. Total Anggota
            val totalMembers = BookRepository.getAllMembers().size

            // 2. Pending Requests (Gunakan fetchPendingRequests untuk data realtime dari API)
            val pendingRequests = BookRepository.fetchPendingRequests().size

            // 3. [UPDATED] Buku Dipinjam - Ambil dari Transaksi (API)
            val borrowedBooksCount = BookRepository.getBorrowedBooksCount()

            // 4. Total Buku
            val allBooks = BookRepository.getAllBooks()
            val totalBooks = allBooks.size

            // Set teks ke TextViews
            tvTotalMembers.text = totalMembers.toString()
            tvPendingRequests.text = pendingRequests.toString()
            tvBorrowedBooks.text = borrowedBooksCount.toString() // Gunakan count dari transaksi
            tvTotalBooks.text = totalBooks.toString()
        }
    }

    /**
     * Mengambil daftar buku terpopuler dan menampilkan visualisasi bar (grafik batang sederhana) di LinearLayout.
     * @param container LinearLayout tempat bar visualisasi akan ditambahkan.
     */
    private fun displayTopBooks(container: LinearLayout) {
        // Mengambil dan mengurutkan buku terpopuler (Map<Judul, JumlahPinjaman>)
        val topBooks = BookRepository.getTopBooks().entries.sortedByDescending { it.value }
        // Menentukan jumlah pinjaman maksimum untuk skala visualisasi
        val maxCount = topBooks.firstOrNull()?.value ?: 1

        topBooks.forEach { (title, count) ->
            // Container horizontal untuk satu baris (Judul + Bar + Jumlah)
            val barContainer = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 8 }
            }

            // Text Judul Buku (menggunakan 40% lebar)
            val titleView = TextView(context).apply {
                text = title
                width = 0
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.4f // 40% lebar
                ).apply {
                    setMargins(16, 4, 0, 4)
                }
                setTextColor(Color.parseColor("#212121"))
            }
            barContainer.addView(titleView)

            // Bar Visualisasi (menggunakan sisa lebar, diskalakan dengan maxCount)
            val barWidthWeight = count.toFloat() / maxCount.toFloat() * 0.6f
            val barView = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    24, // Tinggi bar
                    barWidthWeight // Berat untuk menentukan lebar relatif
                ).apply {
                    setMargins(16, 4, 0, 4)
                }
                setBackgroundColor(resources.getColor(R.color.primary_blue))
            }
            barContainer.addView(barView)

            // Jumlah (Pinjaman)
            val countView = TextView(context).apply {
                text = count.toString()
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 4, 0, 4)
                }
                setTextColor(resources.getColor(R.color.primary_blue_dark))
                setTypeface(null, Typeface.BOLD)
            }
            barContainer.addView(countView)

            container.addView(barContainer)
        }
    }
}