package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.NotificationAdapter
import com.caffeinatedr4t.tamanbacaan.data.BookRepository

/**
 * Fragment untuk manajemen Pengumuman/Kegiatan (Events) oleh Admin.
 * Menyediakan form untuk membuat pengumuman baru dan menampilkan daftar pengumuman terkirim.
 */
class EventManagementFragment : Fragment() {

    // UI: Form Pengumuman
    private lateinit var etEventTitle: EditText // Input Judul Kegiatan
    private lateinit var etEventMessage: EditText // Input Deskripsi/Pesan Kegiatan
    private lateinit var btnPostEvent: Button // Tombol Kirim Pengumuman

    // UI: Daftar Pengumuman
    private lateinit var recyclerView: RecyclerView // RecyclerView untuk menampilkan daftar pengumuman
    private lateinit var adapter: NotificationAdapter // Adapter yang digunakan kembali untuk menampilkan notifikasi event

    /**
     * Membuat dan mengembalikan hierarki tampilan fragmen.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_event_management, container, false)
    }

    /**
     * Dipanggil setelah `onCreateView()`.
     * Menginisialisasi semua View, menyiapkan RecyclerView, dan memuat data awal.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi View Form
        etEventTitle = view.findViewById(R.id.etEventTitle)
        etEventMessage = view.findViewById(R.id.etEventMessage)
        btnPostEvent = view.findViewById(R.id.btnPostEvent)
        // Inisialisasi View RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewEvents)

        setupRecyclerView()
        loadEvents()

        // Listener untuk tombol kirim pengumuman
        btnPostEvent.setOnClickListener {
            postNewEvent()
        }
    }

    /**
     * Menyiapkan RecyclerView untuk menampilkan daftar pengumuman.
     */
    private fun setupRecyclerView() {
        // Menggunakan NotificationAdapter; list buku pinjaman diisi kosong
        adapter = NotificationAdapter(emptyList(), BookRepository.getAllEvents())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    /**
     * Memuat daftar semua pengumuman dari BookRepository dan memperbarui adapter.
     */
    private fun loadEvents() {
        // Membuat ulang adapter dengan data terbaru dari repository
        adapter = NotificationAdapter(emptyList(), BookRepository.getAllEvents())
        recyclerView.adapter = adapter
    }

    /**
     * Mengambil input judul dan pesan, memvalidasi, dan menambahkan pengumuman baru ke repository.
     */
    private fun postNewEvent() {
        val title = etEventTitle.text.toString().trim()
        val message = etEventMessage.text.toString().trim()

        // Validasi input
        if (title.isEmpty() || message.isEmpty()) {
            Toast.makeText(context, "Judul dan deskripsi tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            return
        }

        // Menyimpan event baru ke repository (simulasi)
        BookRepository.addEvent(title, message)

        Toast.makeText(context, "Pengumuman berhasil dikirim!", Toast.LENGTH_SHORT).show()

        // Kosongkan form dan refresh list
        etEventTitle.text.clear()
        etEventMessage.text.clear()
        loadEvents()
    }
}