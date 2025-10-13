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
import com.caffeinatedr4t.tamanbacaan.adapters.RegistrationRequestAdapter // BARU
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.data.RegistrationRequest

class VerificationRequestsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: RegistrationRequestAdapter
    private val requestsList = mutableListOf<RegistrationRequest>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Menggunakan layout daftar anggota yang sama, namun memuat data berbeda
        return inflater.inflate(R.layout.fragment_admin_member_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewMembers)

        setupRecyclerView()
        loadRegistrationRequests()
    }

    private fun setupRecyclerView() {
        requestAdapter = RegistrationRequestAdapter(requestsList,
            onApprove = { request -> handleApproval(request, true) },
            onReject = { request -> handleApproval(request, false) }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = requestAdapter
        }
    }

    private fun loadRegistrationRequests() {
        val requests = BookRepository.getRegistrationRequests()
        requestAdapter.updateData(requests)

        if (requests.isEmpty()) {
            Toast.makeText(context, "Tidak ada permintaan registrasi baru.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleApproval(request: RegistrationRequest, isApproved: Boolean) {
        val result: Boolean
        val message: String

        if (isApproved) {
            // Req: Memverifikasi data anggota (NIK, RT/RW, anak/ortu)
            result = BookRepository.approveRegistration(request.requestId)
            message = if (result) "Registrasi Anggota '${request.fullName}' disetujui dan diverifikasi!" else "Gagal menyetujui registrasi."
        } else {
            result = BookRepository.rejectRegistration(request.requestId)
            message = if (result) "Registrasi Anggota ditolak." else "Gagal menolak registrasi."
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        loadRegistrationRequests() // Muat ulang daftar
    }
}