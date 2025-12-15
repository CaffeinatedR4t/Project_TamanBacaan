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

// Adapter untuk menampilkan daftar buku di sisi pengguna.
class BookAdapter(
    private val books: List<Book>, // Daftar buku.
    private val onBookClick: (Book) -> Unit // Fungsi saat item buku di-klik.
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    // Membuat ViewHolder baru.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    // Menghubungkan data dengan ViewHolder.
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    // Mengembalikan jumlah total buku.
    override fun getItemCount(): Int = books.size

    // Kelas ViewHolder untuk satu item buku.
    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Inisialisasi view dari layout.
        private val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        private val bookAuthor: TextView = itemView.findViewById(R.id.bookAuthor)
        private val bookDescription: TextView = itemView.findViewById(R.id.bookDescription)
        private val bookCategory: TextView = itemView.findViewById(R.id.bookCategory)
        private val statusText: TextView = itemView.findViewById(R.id.statusText)
        private val actionButton: Button = itemView.findViewById(R.id.actionButton)
        private val bookmarkButton: ImageView = itemView.findViewById(R.id.bookmarkButton)

        // Mengisi data buku ke view.
        fun bind(book: Book) {
            bookTitle.text = book.title
            bookAuthor.text = book.author
            bookDescription.text = book.description
            bookCategory.text = book.category

            // Mengatur teks dan warna status ketersediaan buku.
            statusText.text = book.getAvailabilityStatus()
            statusText.setTextColor(ContextCompat.getColor(itemView.context, book.getStatusColor()))

            // Logika untuk tombol aksi (Pinjam/Kembalikan).
            when {
                // Jika buku sedang dipinjam, tombol menjadi "Kembalikan".
                book.isBorrowed -> {
                    actionButton.text = "Return Book (Confirm)"
                    actionButton.isEnabled = true
                }
                // Jika buku tersedia, tombol menjadi "Request Pinjam".
                book.isAvailable -> {
                    actionButton.text = "Request Pinjam"
                    actionButton.isEnabled = true
                }
                // Jika tidak tersedia.
                else -> {
                    // Cek apakah buku sedang dalam proses request.
                    val isPending = BookRepository.getPendingRequests().any { it.book.id == book.id }
                    if (isPending) {
                        actionButton.text = "Menunggu Persetujuan"
                    } else {
                        actionButton.text = "Stok Habis"
                    }
                    actionButton.isEnabled = false
                }
            }

            // Mengatur ikon bookmark (terisi atau kosong).
            bookmarkButton.setImageResource(
                if (book.isBookmarked) R.drawable.ic_bookmark_filled
                else R.drawable.ic_bookmark
            )

            // Aksi saat item buku di-klik: Buka detail buku.
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, BookDetailActivity::class.java)
                intent.putExtra(Constants.EXTRA_BOOK_ID, book.id)
                itemView.context.startActivity(intent)
            }

            // Aksi saat tombol aksi (Pinjam/Kembalikan) di-klik.
            actionButton.setOnClickListener {
                handleBookAction(book)
            }

            // Aksi saat tombol bookmark di-klik.
            bookmarkButton.setOnClickListener {
                // Mengubah status bookmark melalui repository (local state only).
                BookRepository.toggleBookmarkStatus(book.id)
                
                // Toggle UI immediately
                val isNowBookmarked = !book.isBookmarked
                book.isBookmarked = isNowBookmarked
                
                bookmarkButton.setImageResource(
                    if (isNowBookmarked) R.drawable.ic_bookmark_filled
                    else R.drawable.ic_bookmark
                )

                // Menampilkan pesan toast.
                val message = if (isNowBookmarked) "Ditambahkan ke bookmark" else "Dihapus dari bookmark"
                Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
            }
        }

        // Fungsi untuk menangani aksi peminjaman atau pengembalian buku.
        private fun handleBookAction(book: Book) {
            when {
                // Jika mengembalikan buku.
                book.isBorrowed -> {
                    Toast.makeText(itemView.context, "Mengirim konfirmasi pengembalian buku '${book.title}' ke server...", Toast.LENGTH_SHORT).show()
                    // Update local state only (actual API call would be in transaction management)
                    BookRepository.updateBookLocalState(book.id, isAvailable = true, isBorrowed = false)
                    book.isBorrowed = false
                    book.isAvailable = true
                    Toast.makeText(itemView.context, "Pengembalian berhasil dikonfirmasi secara online!", Toast.LENGTH_SHORT).show()
                    notifyItemChanged(adapterPosition)
                }
                // Jika meminta untuk meminjam buku.
                book.isAvailable -> {
                    // FIX: Ganti logic Borrow menjadi Request Pinjam (memerlukan persetujuan Admin)
                    val memberId = "M001"
                    val memberName = "User Test"

                    if (BookRepository.addPendingRequest(book, memberName, memberId)) {
                        Toast.makeText(itemView.context, "Permintaan pinjaman untuk '${book.title}' berhasil dikirim. Menunggu persetujuan Pengelola!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(itemView.context, "Gagal: Buku sudah dalam permintaan atau tidak tersedia.", Toast.LENGTH_SHORT).show()
                    }
                    notifyItemChanged(adapterPosition) // Refresh untuk menampilkan status 'Menunggu Persetujuan'.
                }
            }
        }
    }
}