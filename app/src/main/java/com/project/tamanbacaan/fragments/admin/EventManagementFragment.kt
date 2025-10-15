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
import com.caffeinatedr4t.tamanbacaan.data.BookRepository // Kita akan tambah fungsi di sini nanti
import com.caffeinatedr4t.tamanbacaan.models.EventNotification

class EventManagementFragment : Fragment() {

    private lateinit var etEventTitle: EditText
    private lateinit var etEventMessage: EditText
    private lateinit var btnPostEvent: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_event_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etEventTitle = view.findViewById(R.id.etEventTitle)
        etEventMessage = view.findViewById(R.id.etEventMessage)
        btnPostEvent = view.findViewById(R.id.btnPostEvent)
        recyclerView = view.findViewById(R.id.recyclerViewEvents)

        setupRecyclerView()
        loadEvents()

        btnPostEvent.setOnClickListener {
            postNewEvent()
        }
    }

    private fun setupRecyclerView() {
        // Kita bisa gunakan ulang NotificationAdapter untuk menampilkan daftar event
        // Kita hanya perlu mengisi list buku pinjaman dengan list kosong
        adapter = NotificationAdapter(emptyList(), BookRepository.getAllEvents())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    private fun loadEvents() {
        // Cukup buat ulang adapter dengan data terbaru
        adapter = NotificationAdapter(emptyList(), BookRepository.getAllEvents())
        recyclerView.adapter = adapter
    }

    private fun postNewEvent() {
        val title = etEventTitle.text.toString().trim()
        val message = etEventMessage.text.toString().trim()

        if (title.isEmpty() || message.isEmpty()) {
            Toast.makeText(context, "Judul dan deskripsi tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            return
        }

        // Logika untuk menyimpan event (simulasi di BookRepository)
        BookRepository.addEvent(title, message)

        Toast.makeText(context, "Pengumuman berhasil dikirim!", Toast.LENGTH_SHORT).show()

        // Kosongkan form dan refresh list
        etEventTitle.text.clear()
        etEventMessage.text.clear()
        loadEvents()
    }
}