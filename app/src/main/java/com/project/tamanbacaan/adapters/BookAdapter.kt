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
import com.caffeinatedr4t.tamanbacaan.data.BookRepository // Import Repository

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

            // --- FIX: Logic Pinjaman menjadi Request Pinjam (memerlukan persetujuan Admin) ---
            when {
                // Pinjaman Aktif (Hanya bisa dikembalikan)
                book.isBorrowed -> {
                    actionButton.text = "Return Book (Confirm)"
                    actionButton.isEnabled = true
                }
                // Tersedia (Bisa di-Request)
                book.isAvailable -> {
                    actionButton.text = "Request Pinjam"
                    actionButton.isEnabled = true
                }
                // Tidak Tersedia (Jika sudah di-request atau stok habis)
                else -> {
                    // Cek apakah buku sedang dalam proses request (hanya simulasi)
                    val isPending = BookRepository.getPendingRequests().any { it.book.id == book.id }
                    if (isPending) {
                        actionButton.text = "Menunggu Persetujuan"
                    } else {
                        actionButton.text = "Stok Habis"
                    }
                    actionButton.isEnabled = false
                }
            }
            // ---------------------------------------------------------------------------------

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
                    // Req. Anggota: Mengembalikan buku (online confirmation)
                    Toast.makeText(itemView.context, "Mengirim konfirmasi pengembalian buku '${book.title}' ke server...", Toast.LENGTH_SHORT).show()

                    // Simulasi Sukses:
                    BookRepository.updateBook(book.copy(isBorrowed = false, isAvailable = true))
                    Toast.makeText(itemView.context, "Pengembalian berhasil dikonfirmasi secara online!", Toast.LENGTH_SHORT).show()
                    notifyItemChanged(adapterPosition)
                }
                book.isAvailable -> {
                    // FIX: Ganti logic Borrow menjadi Request Pinjam (memerlukan persetujuan Admin)
                    val memberId = "M001"
                    val memberName = "User Test"

                    if (BookRepository.addPendingRequest(book, memberName, memberId)) {
                        Toast.makeText(itemView.context, "Permintaan pinjaman untuk '${book.title}' berhasil dikirim. Menunggu persetujuan Pengelola!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(itemView.context, "Gagal: Buku sudah dalam permintaan atau tidak tersedia.", Toast.LENGTH_SHORT).show()
                    }
                    // TIDAK ADA PERUBAHAN STATUS LOKAL
                    notifyItemChanged(adapterPosition) // Refresh untuk menampilkan 'Menunggu Persetujuan'
                }
            }
        }
    }
}