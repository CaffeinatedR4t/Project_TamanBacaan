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

// Adapter untuk menampilkan daftar buku di sisi admin.
class AdminBookAdapter(
    private val books: List<Book>, // Daftar buku yang akan ditampilkan.
    private val onDeleteClick: (Book) -> Unit // Fungsi yang akan dipanggil saat tombol hapus di-klik.
) : RecyclerView.Adapter<AdminBookAdapter.AdminBookViewHolder>() {

    // Membuat ViewHolder baru saat RecyclerView membutuhkannya.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminBookViewHolder {
        // Inflate layout item_admin_book.xml untuk setiap item dalam daftar.
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_book, parent, false)
        return AdminBookViewHolder(view)
    }

    // Menghubungkan data buku dengan ViewHolder pada posisi tertentu.
    override fun onBindViewHolder(holder: AdminBookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    // Mengembalikan jumlah total item dalam daftar.
    override fun getItemCount(): Int = books.size

    // Kelas dalam yang merepresentasikan satu item dalam RecyclerView.
    inner class AdminBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Inisialisasi view dari layout item.
        private val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        private val bookAuthor: TextView = itemView.findViewById(R.id.bookAuthor)
        private val bookDescription: TextView = itemView.findViewById(R.id.bookDescription)
        private val bookCategory: TextView = itemView.findViewById(R.id.bookCategory)
        private val stockStatus: TextView = itemView.findViewById(R.id.stockStatus)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        // Fungsi untuk mengisi data buku ke dalam view.
        fun bind(book: Book) {
            bookTitle.text = book.title
            bookAuthor.text = book.author
            bookDescription.text = book.description
            bookCategory.text = book.category

            // Menampilkan status stok buku.
            val stockText = when {
                book.isBorrowed -> "Dipinjam"
                book.isAvailable -> "Tersedia"
                else -> "Stok Habis"
            }

            // Menentukan warna teks status berdasarkan ketersediaan.
            val stockColor = when {
                book.isBorrowed -> android.R.color.holo_orange_dark
                book.isAvailable -> android.R.color.holo_green_dark
                else -> android.R.color.holo_red_dark
            }

            stockStatus.text = stockText
            stockStatus.setTextColor(ContextCompat.getColor(itemView.context, stockColor))

            // Aksi saat tombol Edit di-klik: Navigasi ke EditBookActivity.
            btnEdit.setOnClickListener {
                val intent = Intent(itemView.context, EditBookActivity::class.java)
                intent.putExtra(Constants.EXTRA_BOOK_ID, book.id)
                itemView.context.startActivity(intent)
            }

            // Aksi saat tombol Delete di-klik.
            btnDelete.setOnClickListener {
                onDeleteClick(book)
            }
        }
    }
}