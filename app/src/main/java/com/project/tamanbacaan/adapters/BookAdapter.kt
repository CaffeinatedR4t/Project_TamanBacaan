package com.caffeinatedr4t.tamanbacaan.adapters

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.activities.BookDetailActivity
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.utils.Constants

class BookAdapter(
    private val books: List<Book>,
    private val onActionClick: (Book) -> Unit,
    private val onBookmarkClick: (Book) -> Unit = {}
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
        private val bookCover: ImageView = itemView.findViewById(R.id.bookCover)

        fun bind(book: Book) {
            bookTitle.text = book.title
            bookAuthor.text = book.author
            bookDescription.text = book.description
            bookCategory.text = book.category

            Glide.with(itemView.context)
                .load(book.coverUrl.ifEmpty { R.drawable.ic_book_placeholder })
                .placeholder(R.drawable.ic_book_placeholder)
                .into(bookCover)

            // [BARU] Set Icon Bookmark berdasarkan status
            if (book.isBookmarked) {
                bookmarkButton.setImageResource(R.drawable.ic_bookmark_filled)
            } else {
                bookmarkButton.setImageResource(R.drawable.ic_bookmark)
            }

            // [BARU] Listener Bookmark
            bookmarkButton.setOnClickListener {
                // Optimistic UI Update (Ganti icon langsung)
                book.isBookmarked = !book.isBookmarked
                notifyItemChanged(adapterPosition)

                // Panggil callback agar Activity/Fragment/Repo memproses ke API
                onBookmarkClick(book)
            }

            val currentStatus = book.status

            when (currentStatus) {
                "PENDING" -> {
                    actionButton.text = "Menunggu Persetujuan"
                    actionButton.isEnabled = false
                    actionButton.setBackgroundColor(Color.GRAY)
                    statusText.text = "Status: Menunggu"
                    statusText.setTextColor(Color.parseColor("#FFA500"))
                }
                "BORROWED" -> {
                    actionButton.text = "Kembalikan Buku"
                    actionButton.isEnabled = true
                    actionButton.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.primary_blue))
                    statusText.text = "Status: Dipinjam"
                    statusText.setTextColor(Color.BLUE)

                    actionButton.setOnClickListener {
                        onActionClick(book)
                    }
                }
                else -> {
                    // Tersedia / Returned
                    if (book.isAvailable) {
                        actionButton.text = "Request Pinjam"
                        actionButton.isEnabled = true
                        actionButton.setBackgroundResource(R.drawable.button_primary)
                        statusText.text = "Tersedia"
                        statusText.setTextColor(Color.GREEN)

                        actionButton.setOnClickListener {
                            onActionClick(book)
                        }
                    } else {
                        actionButton.text = "Stok Habis"
                        actionButton.isEnabled = false
                        actionButton.setBackgroundColor(Color.GRAY)
                        statusText.text = "Tidak Tersedia"
                        statusText.setTextColor(Color.RED)
                    }
                }
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, BookDetailActivity::class.java)
                intent.putExtra(Constants.EXTRA_BOOK_ID, book.id)
                itemView.context.startActivity(intent)
            }
        }
    }
}