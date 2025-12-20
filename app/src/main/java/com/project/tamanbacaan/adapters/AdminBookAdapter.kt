package com.caffeinatedr4t.tamanbacaan.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.activities.EditBookActivity
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.utils.Constants

class AdminBookAdapter(
    private val books: List<Book>,
    private val onDeleteClick: (Book) -> Unit
) : RecyclerView.Adapter<AdminBookAdapter.AdminBookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminBookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_book, parent, false)
        return AdminBookViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminBookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size

    inner class AdminBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        private val bookAuthor: TextView = itemView.findViewById(R.id.bookAuthor)
        private val bookDescription: TextView = itemView.findViewById(R.id.bookDescription)
        private val bookCategory: TextView = itemView.findViewById(R.id.bookCategory)
        private val stockStatus: TextView = itemView.findViewById(R.id.stockStatus)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(book: Book) {
            bookTitle.text = book.title
            bookAuthor.text = book.author
            bookDescription.text = book.description
            bookCategory.text = book.category

            val stockText = when {
                book.isBorrowed -> "Dipinjam"
                book.isAvailable -> "Tersedia"
                else -> "Stok Habis"
            }

            val stockColor = when {
                book.isBorrowed -> android.R.color.holo_orange_dark
                book.isAvailable -> android.R.color.holo_green_dark
                else -> android.R.color.holo_red_dark
            }

            stockStatus.text = stockText
            stockStatus.setTextColor(ContextCompat.getColor(itemView.context, stockColor))

            // [PERBAIKAN UTAMA] Kirim semua data buku ke EditActivity
            btnEdit.setOnClickListener {
                val intent = Intent(itemView.context, EditBookActivity::class.java)
                intent.putExtra(Constants.EXTRA_BOOK_ID, book.id)
                // Kirim detail agar form langsung terisi & tidak error "not found"
                intent.putExtra("EXTRA_TITLE", book.title)
                intent.putExtra("EXTRA_AUTHOR", book.author)
                intent.putExtra("EXTRA_DESCRIPTION", book.description)
                intent.putExtra("EXTRA_CATEGORY", book.category)
                intent.putExtra("EXTRA_ISBN", book.isbn)
                intent.putExtra("EXTRA_PUBLICATION_YEAR", book.publicationYear)
                intent.putExtra("EXTRA_COVER_URL", book.coverUrl)
                intent.putExtra("EXTRA_STOCK", book.stock) // Penting agar stok tidak hilang
                intent.putExtra("EXTRA_TOTAL_COPIES", book.totalCopies)
                intent.putExtra("EXTRA_IS_AVAILABLE", book.isAvailable)
                intent.putExtra("EXTRA_IS_BORROWED", book.isBorrowed)

                itemView.context.startActivity(intent)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(book)
            }
        }
    }
}