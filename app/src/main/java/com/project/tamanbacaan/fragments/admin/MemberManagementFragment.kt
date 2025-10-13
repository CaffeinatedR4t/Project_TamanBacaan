package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView // FIX: Tambahkan import TextView
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
    private val memberList = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Menggunakan layout daftar anggota yang sama
        return inflater.inflate(R.layout.fragment_admin_member_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewMembers)

        // FIX: Inisialisasi TextView untuk Judul List yang baru ditambahkan
        val tvListTitle: TextView = view.findViewById(R.id.tvListTitle)
        // Menampilkan jumlah anggota (Req: Cek jumlah user)
        tvListTitle.text = "Daftar Anggota Aktif (${BookRepository.getAllMembers().size} Pengguna)"

        setupRecyclerView()
        loadMembers()
    }

    private fun setupRecyclerView() {
        memberAdapter = MemberAdapter(memberList,
            onEdit = { user -> handleEdit(user) },
            onRemove = { user -> handleRemove(user) }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = memberAdapter
        }
    }

    private fun loadMembers() {
        memberAdapter.updateData(BookRepository.getAllMembers())
    }

    // --- CRUD Logic Handlers ---

    private fun handleEdit(user: User) {
        // Req: Admin bisa EDIT User
        Toast.makeText(context, "Membuka form edit untuk Anggota: ${user.fullName}", Toast.LENGTH_SHORT).show()

        // Simulasi Edit: Mengubah NIK
        val editedUser = user.copy(nik = "UPDATED_ADMIN_NIK_${System.currentTimeMillis()}")
        BookRepository.updateMember(editedUser)
        loadMembers() // Refresh list
        Toast.makeText(context, "Simulasi: NIK ${user.fullName} diperbarui.", Toast.LENGTH_SHORT).show()
    }

    private fun handleRemove(user: User) {
        // Req: Admin bisa REMOVE User
        if (BookRepository.deleteMember(user.id)) {
            Toast.makeText(context, "Anggota ${user.fullName} berhasil dihapus permanen.", Toast.LENGTH_LONG).show()
            loadMembers() // Refresh list
        } else {
            Toast.makeText(context, "Gagal menghapus anggota.", Toast.LENGTH_SHORT).show()
        }
    }
}