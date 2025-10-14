package com.caffeinatedr4t.tamanbacaan.fragments // Ubah package ke fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment // Ubah menjadi Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.NotificationAdapter
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.models.EventNotification

// Ganti dari AppCompatActivity() menjadi Fragment()
class NotificationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter

    // Menggunakan onCreateView (standar Fragment)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    // Menggunakan onViewCreated untuk inisialisasi view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerNotifications)
        // Menggunakan context dari fragment
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 🔹 Dummy data buku yang sedang dipinjam
        val borrowedBooks = listOf(
            Book(
                id = "5",
                title = "Atomic Habits",
                author = "James Clear",
                description = "Tiny changes, remarkable results.",
                coverUrl = "",
                category = "Self-Help",
                isBorrowed = true,
                isAvailable = false,
                borrowedDate = "01/10/2025",
                dueDate = "15/10/2025"
            )
        )

        // 🔹 Dummy data notifikasi event
        val eventNotifications = listOf(
            EventNotification(
                id = "1",
                title = "Bedah Buku 'Laut Bercerita'",
                message = "Ikuti bedah buku bersama penulis Leila S. Chudori pada 10 Oktober 2025!",
                date = "05/10/2025"
            ),
            EventNotification(
                id = "2",
                title = "Diskon Sewa Buku 50%",
                message = "Nikmati diskon 50% untuk semua kategori buku hingga 12 Oktober 2025!",
                date = "07/10/2025"
            )
        )

        adapter = NotificationAdapter(borrowedBooks, eventNotifications)
        recyclerView.adapter = adapter
    }
}