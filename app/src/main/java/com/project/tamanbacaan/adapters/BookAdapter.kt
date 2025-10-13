package com.caffeinatedr4t.tamanbacaan.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.activities.BookDetailActivity
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.utils.Constants

class BookAdapter(
    private val books: List<Book>,
    private val onBookClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        private val bookAuthor: TextView = itemView.findViewById(R.id.bookAuthor)
        private val bookDescription: TextView = itemView.findViewById(R.id.bookDescription)
        private val bookCategory: TextView = itemView.findViewById(R.id.bookCategory)
        private val statusText: TextView = itemView.findViewById(R.id.statusText)
        private val actionButton: Button = itemView.findViewById(R.id.actionButton)
        private val bookmarkButton: ImageView = itemView.findViewById(R.id.bookmarkButton)

        fun bind(book: Book) {
            bookTitle.text = book.title
            bookAuthor.text = book.author
            bookDescription.text = book.description
            bookCategory.text = book.category

            // Set status
            statusText.text = book.getAvailabilityStatus()
            statusText.setTextColor(ContextCompat.getColor(itemView.context, book.getStatusColor()))

            // Set action button
            when {
                book.isBorrowed -> {
                    actionButton.text = "Return Book (Confirm)" // Clarification: Online Confirmation
                    actionButton.isEnabled = true
                }
                book.isAvailable -> {
                    actionButton.text = "Borrow Book"
                    actionButton.isEnabled = true
                }
                else -> {
                    actionButton.text = "Not Available"
                    actionButton.isEnabled = false
                }
            }

            // Set bookmark state
            bookmarkButton.setImageResource(
                if (book.isBookmarked) R.drawable.ic_bookmark_filled
                else R.drawable.ic_bookmark
            )

            // Click listeners
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, BookDetailActivity::class.java)
                intent.putExtra(Constants.EXTRA_BOOK_ID, book.id)
                itemView.context.startActivity(intent)
            }

            actionButton.setOnClickListener {
                // Req. Anggota: Mengembalikan buku (online confirmation)
                handleBookAction(book)
            }

            bookmarkButton.setOnClickListener {
                book.isBookmarked = !book.isBookmarked
                notifyItemChanged(adapterPosition)
            }
        }

        private fun handleBookAction(book: Book) {
            when {
                book.isBorrowed -> {
                    // PANGGIL API UNTUK KONFIRMASI PENGEMBALIAN BUKU
                    Toast.makeText(itemView.context, "Mengirim konfirmasi pengembalian buku '${book.title}' ke server...", Toast.LENGTH_SHORT).show()

                    // Simulasi Sukses:
                    book.isBorrowed = false
                    book.isAvailable = true
                    Toast.makeText(itemView.context, "Pengembalian berhasil dikonfirmasi secara online!", Toast.LENGTH_SHORT).show()
                }
                book.isAvailable -> {
                    // Borrow book logic
                    book.isBorrowed = true
                    book.isAvailable = false
                    Toast.makeText(itemView.context, "Buku '${book.title}' dipinjam (simulasi).", Toast.LENGTH_SHORT).show()
                }
            }
            notifyItemChanged(adapterPosition)
        }
    }
}