package com.caffeinatedr4t.tamanbacaan.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.utils.Constants
import com.bumptech.glide.Glide

/**
 * Activity untuk menampilkan detail dari sebuah buku.
 * Halaman ini juga memungkinkan pengguna untuk memberikan rating dan ulasan.
 */
class BookDetailActivity : AppCompatActivity() {

    // Variabel ini berisi daftar buku dummy sebagai sumber data sementara.
    // Idealnya, data ini akan diambil dari database atau API.
    private val sampleBooks = listOf(
        Book("1", "To Kill a Mockingbird", "Harper Lee", "A classic novel...", "", "Fiction", avgRating = 4.5f, totalReviews = 120, synopsis = "Di kota fiksi Maycomb, Alabama, selama Depresi Hebat, To Kill a Mockingbird bercerita tentang seorang pengacara bernama Atticus Finch yang membela seorang pria kulit hitam yang dituduh memperkosa seorang wanita kulit putih."),
        Book("2", "1984", "George Orwell", "A dystopian social...", "", "Fiction", avgRating = 4.8f, totalReviews = 250, synopsis = "Dunia masa depan di mana masyarakat dimanipulasi oleh partai politik dan dipantau oleh Big Brother."),
        Book("3", "The Great Gatsby", "F. Scott Fitzgerald", "The story of Jay Gatsby's...", "", "Classic", avgRating = 4.1f, totalReviews = 90, synopsis = "Kisah Jay Gatsby dan cintanya yang tak terbalas kepada Daisy Buchanan di tahun 1920-an.")
    )

    /**
     * Fungsi yang dipanggil saat Activity pertama kali dibuat.
     * Fungsi ini bertanggung jawab untuk inisialisasi layout dan pengambilan data.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        // Mengambil ID buku yang dikirim dari activity sebelumnya melalui Intent.
        val bookId = intent.getStringExtra(Constants.EXTRA_BOOK_ID)

        // Memeriksa apakah bookId null atau tidak. Jika null, activity akan ditutup.
        if (bookId == null) {
            Toast.makeText(this, "Book ID tidak ditemukan.", Toast.LENGTH_SHORT).show()
            finish() // Menutup activity jika tidak ada ID buku.
            return
        }

        // Mencari data buku di dalam daftar dummy berdasarkan bookId.
        // Ini adalah simulasi pengambilan data dari sumber data.
        val book = sampleBooks.find { it.id == bookId }

        // Jika buku dengan ID yang sesuai ditemukan, tampilkan detailnya.
        if (book != null) {
            displayBookDetails(book)
            setupReviewSubmission()
        } else {
            // Jika buku tidak ditemukan, tampilkan pesan error dan tutup activity.
            Toast.makeText(this, "Buku tidak ditemukan.", Toast.LENGTH_SHORT).show()
            finish()
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
        findViewById<TextView>(R.id.detailRating).text = book.avgRating.toString()
        findViewById<RatingBar>(R.id.ratingBarSmall).rating = book.avgRating
        findViewById<TextView>(R.id.detailReviewCount).text = "(${book.totalReviews} Ulasan)"
        findViewById<TextView>(R.id.detailSinopsis).text = book.synopsis

        // Menemukan komponen ImageView untuk cover buku.
        val bookCover: ImageView = findViewById(R.id.bookCover)

        // Menggunakan library Glide untuk memuat gambar cover buku.
        Glide.with(this)
            .load(R.drawable.ic_explore) // Placeholder, idealnya diganti dengan URL gambar dari `book.coverUrl`.
            .placeholder(R.drawable.ic_explore) // Gambar yang ditampilkan saat gambar asli sedang dimuat.
            .into(bookCover) // Target ImageView untuk menampilkan gambar.
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
            val userRating = ratingBarUser.rating
            val userReview = etReview.text.toString().trim()

            // Validasi: pastikan pengguna memberikan rating dan menulis ulasan.
            if (userRating > 0 && userReview.isNotEmpty()) {
                // Simulasi pengiriman ulasan ke backend (sesuai Req. 8).
                Toast.makeText(this, "Ulasan berhasil dikirim! Rating: $userRating", Toast.LENGTH_LONG).show()

                // Mengosongkan kembali input setelah ulasan berhasil dikirim.
                etReview.text.clear()
                ratingBarUser.rating = 0f
            } else {
                // Menampilkan pesan jika input tidak valid.
                Toast.makeText(this, "Mohon berikan rating dan tulis ulasan Anda.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
