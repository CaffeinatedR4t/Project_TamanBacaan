package com.caffeinatedr4t.tamanbacaan.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.utils.Constants
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

/**
 * Activity untuk menampilkan detail dari sebuah buku.
 * Halaman ini juga memungkinkan pengguna untuk memberikan rating dan ulasan.
 */
class BookDetailActivity : AppCompatActivity() {

    // [MODIFIKASI] Simpan bookId di level class agar bisa diakses saat submit review
    private var currentBookId: String? = null

    /**
     * Fungsi yang dipanggil saat Activity pertama kali dibuat.
     * Fungsi ini bertanggung jawab untuk inisialisasi layout dan pengambilan data.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // ✅ ADD THIS - Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Buku"

        // Mengambil ID buku yang dikirim dari activity sebelumnya melalui Intent.
        currentBookId = intent.getStringExtra(Constants.EXTRA_BOOK_ID)

        // Memeriksa apakah bookId null atau tidak.  Jika null, activity akan ditutup.
        if (currentBookId == null) {
            Toast.makeText(this, "Book ID tidak ditemukan.", Toast.LENGTH_SHORT).show()
            finish() // Menutup activity jika tidak ada ID buku.
            return
        }

        // [MODIFIKASI] Panggil fungsi loadBookData terpisah agar bisa direfresh
        loadBookData()

        setupReviewSubmission()
    }

    // ✅ ADD THIS FUNCTION - Handle back button click
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * [BARU] Fungsi untuk memuat data buku dari Repository.
     * Dipisahkan agar bisa dipanggil ulang setelah submit review (untuk update rating realtime).
     */
    private fun loadBookData() {
        lifecycleScope.launch {
            val book = BookRepository.getBookById(currentBookId!!)

            if (book != null) {
                displayBookDetails(book)
            } else {
                Toast.makeText(this@BookDetailActivity, "Buku tidak ditemukan.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Fungsi untuk menampilkan semua detail buku ke komponen UI (TextView, ImageView, dll).
     * @param book Objek buku yang datanya akan ditampilkan.
     */
    private fun displayBookDetails(book: Book) {
        // Menetapkan data buku ke elemen-elemen UI.
        findViewById<TextView>(R.id.detailTitle).text = book.title
        findViewById<TextView>(R.id.detailAuthor).text = book.author

        // [MODIFIKASI] Tampilkan Rating dan Jumlah Review dari Database
        findViewById<TextView>(R.id.detailRating).text = book.avgRating.toString()
        findViewById<RatingBar>(R.id.ratingBarSmall).rating = book.avgRating
        findViewById<TextView>(R.id.detailReviewCount).text = "(${book.totalReviews} Ulasan)"

        findViewById<TextView>(R.id.detailSinopsis).text = book.synopsis

        // Menemukan komponen ImageView untuk cover buku.
        val bookCover: ImageView = findViewById(R.id.bookCover)

        // Menggunakan library Glide untuk memuat gambar cover buku.
        Glide.with(this)
            .load(book.coverUrl.ifEmpty { R.drawable.ic_explore })
            .placeholder(R.drawable.ic_explore)
            .into(bookCover)
    }

    /**
     * Fungsi untuk mengatur logika pengiriman ulasan.
     * Menambahkan listener pada tombol submit.
     */
    private fun setupReviewSubmission() {
        // Inisialisasi komponen UI untuk bagian ulasan.
        val ratingBarUser: RatingBar = findViewById(R.id.ratingBarUser)
        val etReview: EditText = findViewById(R.id.etReview)
        val btnSubmitReview: Button = findViewById(R.id.btnSubmitReview)

        // Menetapkan aksi yang akan dijalankan saat tombol "Submit" ditekan.
        btnSubmitReview.setOnClickListener {
            // Mengambil nilai rating dan teks ulasan dari input pengguna.
            val userRating = ratingBarUser.rating.toDouble() // Convert ke Double untuk API
            val userReview = etReview.text.toString().trim()

            // Validasi: pastikan pengguna memberikan rating dan menulis ulasan.
            if (userRating > 0 && userReview.isNotEmpty()) {

                // Disable tombol sementara agar tidak double click
                btnSubmitReview.isEnabled = false
                btnSubmitReview.text = "Mengirim..."

                // [MODIFIKASI] Panggil API via Repository
                lifecycleScope.launch {
                    val isSuccess = BookRepository.submitReview(currentBookId!!, userRating, userReview)

                    if (isSuccess) {
                        Toast.makeText(this@BookDetailActivity, "Ulasan berhasil dikirim!", Toast.LENGTH_SHORT).show()

                        // Mengosongkan kembali input setelah ulasan berhasil dikirim.
                        etReview.text.clear()
                        ratingBarUser.rating = 0f

                        // [PENTING] Reload data buku agar Avg Rating dan Total Reviews terupdate di layar
                        loadBookData()
                    } else {
                        Toast.makeText(this@BookDetailActivity, "Gagal mengirim ulasan. Cek koneksi internet.", Toast.LENGTH_SHORT).show()
                    }

                    // Enable tombol kembali
                    btnSubmitReview.isEnabled = true
                    btnSubmitReview.text = "Kirim Ulasan"
                }

            } else {
                // Menampilkan pesan jika input tidak valid.
                Toast.makeText(this, "Mohon berikan rating dan tulis ulasan Anda.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}