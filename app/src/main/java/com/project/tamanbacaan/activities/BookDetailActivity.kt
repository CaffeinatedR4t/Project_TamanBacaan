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

class BookDetailActivity : AppCompatActivity() {

    // Dummy data source - ideally fetch from network/database
    private val sampleBooks = listOf(
        Book("1", "To Kill a Mockingbird", "Harper Lee", "A classic novel...", "", "Fiction", avgRating = 4.5f, totalReviews = 120, synopsis = "Di kota fiksi Maycomb, Alabama, selama Depresi Hebat, To Kill a Mockingbird bercerita tentang seorang pengacara bernama Atticus Finch yang membela seorang pria kulit hitam yang dituduh memperkosa seorang wanita kulit putih."),
        Book("2", "1984", "George Orwell", "A dystopian social...", "", "Fiction", avgRating = 4.8f, totalReviews = 250, synopsis = "Dunia masa depan di mana masyarakat dimanipulasi oleh partai politik dan dipantau oleh Big Brother."),
        Book("3", "The Great Gatsby", "F. Scott Fitzgerald", "The story of Jay Gatsby's...", "", "Classic", avgRating = 4.1f, totalReviews = 90, synopsis = "Kisah Jay Gatsby dan cintanya yang tak terbalas kepada Daisy Buchanan di tahun 1920-an.")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        val bookId = intent.getStringExtra(Constants.EXTRA_BOOK_ID)

        if (bookId == null) {
            Toast.makeText(this, "Book ID tidak ditemukan.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Cari buku (Simulasi fetch data)
        val book = sampleBooks.find { it.id == bookId }

        if (book != null) {
            displayBookDetails(book)
            setupReviewSubmission()
        } else {
            Toast.makeText(this, "Buku tidak ditemukan.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayBookDetails(book: Book) {
        findViewById<TextView>(R.id.detailTitle).text = book.title
        findViewById<TextView>(R.id.detailAuthor).text = book.author
        findViewById<TextView>(R.id.detailRating).text = book.avgRating.toString()
        findViewById<RatingBar>(R.id.ratingBarSmall).rating = book.avgRating
        findViewById<TextView>(R.id.detailReviewCount).text = "(${book.totalReviews} Ulasan)"
        findViewById<TextView>(R.id.detailSinopsis).text = book.synopsis

        // Load Cover Image (Menggunakan Glide, saat ini dummy icon)
        val bookCover: ImageView = findViewById(R.id.bookCover)
        Glide.with(this)
            .load(R.drawable.ic_explore) // Ganti dengan book.coverUrl jika ada
            .placeholder(R.drawable.ic_explore)
            .into(bookCover)
    }

    private fun setupReviewSubmission() {
        val ratingBarUser: RatingBar = findViewById(R.id.ratingBarUser)
        val etReview: EditText = findViewById(R.id.etReview)
        val btnSubmitReview: Button = findViewById(R.id.btnSubmitReview)

        btnSubmitReview.setOnClickListener {
            val userRating = ratingBarUser.rating
            val userReview = etReview.text.toString().trim()

            if (userRating > 0 && userReview.isNotEmpty()) {
                // Implementasi logika pengiriman ulasan ke backend (Req. 8)
                Toast.makeText(this, "Ulasan berhasil dikirim! Rating: $userRating", Toast.LENGTH_LONG).show()
                etReview.text.clear()
                ratingBarUser.rating = 0f
            } else {
                Toast.makeText(this, "Mohon berikan rating dan tulis ulasan Anda.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}