package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.PendingRequestAdapter
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.data.PendingRequest
import kotlinx.coroutines.launch

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

        // Panggil fungsi load data
        loadPendingRequests()
    }

    private fun setupRecyclerView() {
        requestAdapter = PendingRequestAdapter(pendingRequests,
            onApprove = { request -> handleApproval(request, true) },
            onReject = { request -> handleApproval(request, false) }
        )

        recyclerViewRequests.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = requestAdapter
        }
    }

    private fun loadPendingRequests() {
        // [PENTING] Gunakan lifecycleScope untuk memanggil fungsi suspend (API)
        lifecycleScope.launch {
            Toast.makeText(context, "Memuat data...", Toast.LENGTH_SHORT).show()

            // Ambil data real dari API via Repository
            val requests = BookRepository.fetchPendingRequests()

            pendingRequests.clear()
            pendingRequests.addAll(requests)
            requestAdapter.notifyDataSetChanged()

            if (requests.isEmpty()) {
                Toast.makeText(context, "Tidak ada permintaan pending.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleApproval(request: PendingRequest, isApproved: Boolean) {
        lifecycleScope.launch {
            val success: Boolean
            val message: String

            if (isApproved) {
                // Panggil API Approve
                success = BookRepository.approveRequestApi(request.requestId)
                message = if (success) "Permintaan disetujui!" else "Gagal menyetujui."
            } else {
                // Panggil API Reject
                success = BookRepository.rejectRequestApi(request.requestId)
                message = if (success) "Permintaan ditolak." else "Gagal menolak."
            }

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            // Refresh list jika sukses
            if (success) {
                loadPendingRequests()
            }
        }
    }
}