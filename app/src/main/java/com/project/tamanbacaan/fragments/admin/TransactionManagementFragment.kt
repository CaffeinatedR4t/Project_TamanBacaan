package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Perlu dependency fragment-ktx
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.PendingRequestAdapter
import com.caffeinatedr4t.tamanbacaan.data.PendingRequest
import com.caffeinatedr4t.tamanbacaan.state.TransactionManagementState
import com.caffeinatedr4t.tamanbacaan.viewmodels.TransactionManagementViewModel

/**
 * Fragment untuk Manajemen Transaksi (Approve/Reject Peminjaman) dengan MVVM.
 */
class TransactionManagementFragment : Fragment() {

    private lateinit var recyclerViewRequests: RecyclerView
    private lateinit var requestAdapter: PendingRequestAdapter
    private val pendingRequests = mutableListOf<PendingRequest>()

    // Inisialisasi ViewModel
    private val viewModel: TransactionManagementViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Pastikan layout yang digunakan benar (sepertinya Anda menggunakan layout yang sama dengan member list atau punya layout sendiri)
        // Jika layoutnya 'fragment_admin_member_list', pastikan ID RecyclerView-nya sesuai.
        return inflater.inflate(R.layout.fragment_admin_member_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sesuaikan ID ini dengan XML Anda.
        // Di file asli Anda menggunakan R.id.recyclerViewMembers, pastikan itu benar untuk layout ini.
        recyclerViewRequests = view.findViewById(R.id.recyclerViewMembers)

        setupRecyclerView()

        // Observe ViewModel State
        observeViewModel()

        // Panggil fungsi load data awal
        viewModel.loadPendingRequests()
    }

    private fun setupRecyclerView() {
        requestAdapter = PendingRequestAdapter(pendingRequests,
            onApprove = { request ->
                // Delegasikan ke ViewModel
                viewModel.processRequest(request.requestId, isApproved = true)
            },
            onReject = { request ->
                // Delegasikan ke ViewModel
                viewModel.processRequest(request.requestId, isApproved = false)
            }
        )

        recyclerViewRequests.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = requestAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TransactionManagementState.Loading -> {
                    // Tampilkan loading indicator jika ada (misal Toast singkat atau ProgressBar)
                    // Toast.makeText(context, "Memproses...", Toast.LENGTH_SHORT).show()
                }
                is TransactionManagementState.SuccessLoad -> {
                    val requests = state.requests
                    pendingRequests.clear()
                    pendingRequests.addAll(requests)
                    requestAdapter.notifyDataSetChanged()

                    if (requests.isEmpty()) {
                        Toast.makeText(context, "Tidak ada permintaan pending.", Toast.LENGTH_SHORT).show()
                    }
                }
                is TransactionManagementState.SuccessOperation -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    // Tidak perlu reload manual karena ViewModel sudah memanggil loadPendingRequests()
                }
                is TransactionManagementState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                }
                is TransactionManagementState.Idle -> {
                    // Do nothing
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Pastikan data selalu fresh saat kembali ke tab ini
        viewModel.loadPendingRequests()
    }
}