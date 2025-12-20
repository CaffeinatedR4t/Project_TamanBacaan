package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.NotificationAdapter
import com.caffeinatedr4t.tamanbacaan.utils.SharedPrefsManager
import com.caffeinatedr4t.tamanbacaan.viewmodel.EventViewModel


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
    private lateinit var viewModel: EventViewModel

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

        // 🔴 WAJIB: inisialisasi View
        etEventTitle = view.findViewById(R.id.etEventTitle)
        etEventMessage = view.findViewById(R.id.etEventMessage)
        btnPostEvent = view.findViewById(R.id.btnPostEvent)
        recyclerView = view.findViewById(R.id.recyclerViewEvents)

        // ViewModel
        viewModel = ViewModelProvider(this)[EventViewModel::class.java]

        // RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Ambil token user
        val token = SharedPrefsManager(requireContext()).getUserToken()

        viewModel.events.observe(viewLifecycleOwner) { events ->
            // UPDATE DI SINI:
            // Kita pass lambda function untuk menangani klik delete
            adapter = NotificationAdapter(
                emptyList(),
                events,
                onDeleteEvent = { eventId ->
                    if (token != null) {
                        viewModel.deleteEvent(token, eventId)
                    }
                }
            )
            recyclerView.adapter = adapter
        }

        viewModel.loadEvents()

        btnPostEvent.setOnClickListener {
            val title = etEventTitle.text.toString().trim()
            val message = etEventMessage.text.toString().trim()
            val token = SharedPrefsManager(requireContext()).getUserToken()

            if (title.isNotEmpty() && message.isNotEmpty() && token != null) {
                viewModel.addEvent(token, title, message)

                etEventTitle.text.clear()
                etEventMessage.text.clear()

                etEventTitle.clearFocus()
                etEventMessage.clearFocus()
            }
        }
    }

}