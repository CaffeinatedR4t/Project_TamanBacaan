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

class TransactionManagementFragment : Fragment() {

    private lateinit var recyclerViewRequests: RecyclerView
    private lateinit var requestAdapter: PendingRequestAdapter
    private val pendingRequests = mutableListOf<PendingRequest>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_member_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewRequests = view.findViewById(R.id.recyclerViewMembers)

        setupRecyclerView()
        loadPendingRequests()
    }

    private fun setupRecyclerView() {
        // Menggunakan Adapter Permintaan Baru
        requestAdapter = PendingRequestAdapter(pendingRequests,
            onApprove = { request -> handleApproval(request, true) },
            onReject = { request -> handleApproval(request, false) }
        )

        recyclerViewRequests.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = requestAdapter
        }
    }

    // Memuat daftar Permintaan Pinjaman Tertunda (Request -> Approval)
    private fun loadPendingRequests() {
        val requests = BookRepository.getPendingRequests()

        requestAdapter.updateData(requests)

        if (requests.isEmpty()) {
            Toast.makeText(context, "Tidak ada permintaan pinjaman yang tertunda.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleApproval(request: PendingRequest, isApproved: Boolean) {
        val result: Boolean
        val message: String

        if (isApproved) {
            // Panggil Repository untuk menyetujui
            result = BookRepository.approveRequest(request.requestId)
            message = if (result) "Permintaan disetujui! Buku '${request.book.title}' telah dipinjamkan." else "Gagal menyetujui permintaan."
        } else {
            // Panggil Repository untuk menolak
            result = BookRepository.rejectRequest(request.requestId)
            message = if (result) "Permintaan pinjaman ditolak." else "Gagal menolak permintaan."
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        loadPendingRequests() // Muat ulang daftar setelah aksi
    }
}