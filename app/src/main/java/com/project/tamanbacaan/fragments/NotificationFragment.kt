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
import com.caffeinatedr4t.tamanbacaan.data.BookRepository // 👈 1. IMPORT REPOSITORY
import com.caffeinatedr4t.tamanbacaan.models.Book
import com.caffeinatedr4t.tamanbacaan.models.EventNotification

class NotificationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerNotifications)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // --- PERUBAHAN UTAMA ADA DI SINI ---

        // 🔹 2. Ambil data buku yang sedang dipinjam dari Repository
        val borrowedBooks = BookRepository.getAllBooks().filter { it.isBorrowed }

        // 🔹 3. Ambil data event yang dibuat admin dari Repository (bukan dummy lagi)
        val eventNotifications = BookRepository.getAllEvents()

        // Set adapter dengan data yang sudah diambil dari Repository
        adapter = NotificationAdapter(borrowedBooks, eventNotifications)
        recyclerView.adapter = adapter
    }
}