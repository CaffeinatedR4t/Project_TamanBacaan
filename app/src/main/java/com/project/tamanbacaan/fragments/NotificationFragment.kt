package com.caffeinatedr4t.tamanbacaan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.NotificationAdapter
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.viewmodels.EventViewModel
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
    private lateinit var viewModel: EventViewModel
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

        viewModel = ViewModelProvider(this)[EventViewModel::class.java]

        recyclerView = view.findViewById(R.id.recyclerNotifications)
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.events.observe(viewLifecycleOwner) { events ->

            // ✅ panggil suspend function dengan coroutine
            viewLifecycleOwner.lifecycleScope.launch {
                val borrowedBooks =
                    BookRepository.getAllBooks().filter { it.isBorrowed }

                adapter = NotificationAdapter(
                    borrowedBooks = borrowedBooks,
                    eventNotifications = events
                )
                recyclerView.adapter = adapter
            }
        }

        // ✅ event load lewat ViewModel
        viewModel.loadEvents()
    }
}