package com.caffeinatedr4t.tamanbacaan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.models.EventNotification

// Adapter untuk menampilkan dua jenis notifikasi: buku pinjaman dan event.
class NotificationAdapter(
    private val borrowedBooks: List<Book>, // Daftar buku yang dipinjam.
    private val eventNotifications: List<EventNotification> // Daftar notifikasi event.
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Konstanta untuk membedakan jenis view.
    private val VIEW_TYPE_BOOK = 1
    private val VIEW_TYPE_EVENT = 2

    // Menentukan jenis view berdasarkan posisi item.
    override fun getItemViewType(position: Int): Int {
        return if (position < borrowedBooks.size) VIEW_TYPE_BOOK else VIEW_TYPE_EVENT
    }

    // Membuat ViewHolder yang sesuai dengan jenis view.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_BOOK) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notification_book, parent, false)
            BookViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notification_event, parent, false)
            EventViewHolder(view)
        }
    }

    // Mengembalikan jumlah total semua notifikasi.
    override fun getItemCount(): Int = borrowedBooks.size + eventNotifications.size

    // Menghubungkan data dengan ViewHolder yang sesuai.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BookViewHolder && position < borrowedBooks.size) {
            holder.bind(borrowedBooks[position])
        } else if (holder is EventViewHolder) {
            val eventIndex = position - borrowedBooks.size
            holder.bind(eventNotifications[eventIndex])
        }
    }

    // ViewHolder untuk notifikasi buku pinjaman.
    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvBookTitle)
        private val tvDueDate: TextView = itemView.findViewById(R.id.tvBookDueDate)

        fun bind(book: Book) {
            tvTitle.text = "📘 ${book.title} - ${book.author}"
            tvDueDate.text = "Batas pengembalian: ${book.dueDate}"
        }
    }

    // ViewHolder untuk notifikasi event.
    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvEventTitle: TextView = itemView.findViewById(R.id.tvEventTitle)
        private val tvEventMessage: TextView = itemView.findViewById(R.id.tvEventMessage)
        private val tvEventDate: TextView = itemView.findViewById(R.id.tvEventDate)

        fun bind(event: EventNotification) {
            tvEventTitle.text = "📅 ${event.title}"
            tvEventMessage.text = event.message
            tvEventDate.text = "Tanggal: ${event.date}"
        }
    }
}
