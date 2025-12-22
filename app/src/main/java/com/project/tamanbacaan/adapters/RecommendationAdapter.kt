package com.caffeinatedr4t.tamanbacaan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.models.Book

class RecommendationAdapter(
    private val books: MutableList<Book>,
    private val onItemClick: (Book) -> Unit
) : RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {

    fun updateData(newBooks: List<Book>) {
        books.clear()
        books.addAll(newBooks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_recommendation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgCover: ImageView = itemView.findViewById(R.id.imgBookCover)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvBookTitle)

        fun bind(book: Book) {
            tvTitle.text = book.title

            // Menggunakan Glide untuk gambar
            Glide.with(itemView.context)
                .load(book.coverUrl)
                .placeholder(R.drawable.ic_book_placeholder)
                .error(R.drawable.ic_book_placeholder)
                .into(imgCover)

            itemView.setOnClickListener { onItemClick(book) }
        }
    }
}