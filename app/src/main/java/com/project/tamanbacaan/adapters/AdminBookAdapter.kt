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

            // Display stock status
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

            // Edit button click - Navigate to EditBookActivity
            btnEdit.setOnClickListener {
                val intent = Intent(itemView.context, EditBookActivity::class.java)
                intent.putExtra(Constants.EXTRA_BOOK_ID, book.id)
                itemView.context.startActivity(intent)
            }

            // Delete button click
            btnDelete.setOnClickListener {
                onDeleteClick(book)
            }
        }
    }
}