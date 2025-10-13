package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.adapters.MemberAdapter
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.models.User

class MemberManagementFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var memberAdapter: MemberAdapter
    private lateinit var tvListTitle: TextView // Declare tvListTitle
    private val memberList = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_member_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewMembers)
        tvListTitle = view.findViewById(R.id.tvListTitle)

        setupRecyclerView()
        loadMembers()
    }

    private fun setupRecyclerView() {
        memberAdapter = MemberAdapter(memberList,
            onVerifyToggle = { user -> handleVerifyToggle(user) }, // FIX: Menggunakan onVerifyToggle
            onRemove = { user -> handleRemove(user) }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = memberAdapter
        }
    }

    private fun loadMembers() {
        val allMembers = BookRepository.getAllMembers()
        memberAdapter.updateData(allMembers)

        // Update Title (Req: Cek jumlah user)
        tvListTitle.text = "Daftar Anggota Aktif (${allMembers.size} Pengguna)"
    }

    // --- NEW Verification Logic ---
    private fun handleVerifyToggle(user: User) {
        val newStatus = !user.isVerified

        // Panggil Repository untuk mengubah status verifikasi
        if (BookRepository.toggleVerificationStatus(user.id)) {
            val action = if (newStatus) "Diverifikasi" else "Dibatalkan verifikasi"
            Toast.makeText(context, "${user.fullName} telah ${action} sebagai warga RT/RW.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Gagal mengubah status verifikasi.", Toast.LENGTH_SHORT).show()
        }
        loadMembers() // Refresh list
    }

    // --- Remove Logic (Tetap) ---
    private fun handleRemove(user: User) {
        if (BookRepository.deleteMember(user.id)) {
            Toast.makeText(context, "Anggota ${user.fullName} berhasil dihapus permanen.", Toast.LENGTH_LONG).show()
            loadMembers()
        } else {
            Toast.makeText(context, "Gagal menghapus anggota.", Toast.LENGTH_SHORT).show()
        }
    }
}