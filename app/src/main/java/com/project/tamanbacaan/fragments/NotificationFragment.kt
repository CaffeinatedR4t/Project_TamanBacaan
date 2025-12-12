package com.caffeinatedr4t.tamanbacaan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.NotificationAdapter
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import kotlinx.coroutines.launch

/**
 * Fragment yang menampilkan daftar notifikasi untuk pengguna.
 * Notifikasi mencakup pengingat pengembalian buku pinjaman dan pengumuman kegiatan/event.
 */
class NotificationFragment : Fragment() {

    // RecyclerView untuk menampilkan daftar notifikasi
    private lateinit var recyclerView: RecyclerView
    // Adapter untuk mengelola data notifikasi (buku pinjaman dan event)
    private lateinit var adapter: NotificationAdapter

    /**
     * Membuat dan mengembalikan hierarki tampilan fragmen.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    /**
     * Dipanggil setelah `onCreateView()`.
     * Menginisialisasi RecyclerView dan memuat data notifikasi dari repositori.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi RecyclerView dan set layout manager
        recyclerView = view.findViewById(R.id.recyclerNotifications)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Load data from API
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 2. Ambil data buku yang sedang dipinjam (isBorrowed = true) dari Repository (API)
                val borrowedBooks = BookRepository.getAllBooks().filter { it.isBorrowed }

                // 3. Ambil data event/pengumuman dari Repository
                val eventNotifications = BookRepository.getAllEvents()

                // Set adapter dengan data yang sudah diambil dari Repository
                adapter = NotificationAdapter(
                    borrowedBooks = borrowedBooks, // Daftar buku pinjaman
                    eventNotifications = eventNotifications // Daftar pengumuman event
                )
                recyclerView.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error
            }
        }
    }
}