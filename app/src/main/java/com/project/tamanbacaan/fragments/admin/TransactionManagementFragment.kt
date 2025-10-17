package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.PendingRequestAdapter
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.data.PendingRequest

/**
 * Fragment untuk manajemen Permintaan Pinjaman (Transaksi) oleh Admin.
 * Menampilkan daftar permintaan pinjaman yang perlu disetujui atau ditolak.
 */
class TransactionManagementFragment : Fragment() {

    // RecyclerView untuk menampilkan daftar permintaan pinjaman
    private lateinit var recyclerViewRequests: RecyclerView
    // Adapter untuk mengelola data permintaan pinjaman
    private lateinit var requestAdapter: PendingRequestAdapter
    // Daftar permintaan pinjaman yang tertunda (diisi dari repository)
    private val pendingRequests = mutableListOf<PendingRequest>()

    /**
     * Membuat dan mengembalikan hierarki tampilan fragmen.
     * Menggunakan layout `fragment_admin_member_list` (layout universal untuk list admin).
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_member_list, container, false)
    }

    /**
     * Dipanggil setelah `onCreateView()`.
     * Menginisialisasi View, menyiapkan RecyclerView, dan memuat data awal.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menggunakan kembali ID yang sama untuk RecyclerView
        recyclerViewRequests = view.findViewById(R.id.recyclerViewMembers)

        setupRecyclerView()
        loadPendingRequests()
    }

    /**
     * Menyiapkan RecyclerView dengan adapter dan callback untuk aksi persetujuan/penolakan.
     */
    private fun setupRecyclerView() {
        // Menggunakan Adapter Permintaan Baru
        requestAdapter = PendingRequestAdapter(pendingRequests,
            onApprove = { request -> handleApproval(request, true) }, // Callback untuk menyetujui
            onReject = { request -> handleApproval(request, false) } // Callback untuk menolak
        )

        recyclerViewRequests.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = requestAdapter
        }
    }

    /**
     * Memuat daftar Permintaan Pinjaman Tertunda dari BookRepository.
     */
    private fun loadPendingRequests() {
        val requests = BookRepository.getPendingRequests()

        // Memperbarui data di adapter
        requestAdapter.updateData(requests)

        // Tampilkan pesan jika tidak ada permintaan
        if (requests.isEmpty()) {
            Toast.makeText(context, "Tidak ada permintaan pinjaman yang tertunda.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Menangani logika persetujuan atau penolakan permintaan pinjaman.
     * @param request Objek PendingRequest yang akan diproses.
     * @param isApproved Boolean, true jika disetujui, false jika ditolak.
     */
    private fun handleApproval(request: PendingRequest, isApproved: Boolean) {
        val result: Boolean
        val message: String

        if (isApproved) {
            // Logika menyetujui permintaan: mengubah status buku menjadi dipinjam.
            result = BookRepository.approveRequest(request.requestId)
            message = if (result) "Permintaan disetujui! Buku '${request.book.title}' telah dipinjamkan." else "Gagal menyetujui permintaan."
        } else {
            // Logika menolak permintaan: mengembalikan status buku menjadi tersedia.
            result = BookRepository.rejectRequest(request.requestId)
            message = if (result) "Permintaan pinjaman ditolak." else "Gagal menolak permintaan."
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        loadPendingRequests() // Muat ulang daftar setelah aksi
    }
}