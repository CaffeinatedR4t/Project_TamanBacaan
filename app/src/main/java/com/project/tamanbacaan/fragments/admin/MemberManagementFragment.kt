package com.caffeinatedr4t.tamanbacaan.fragments. admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan. adapters.MemberAdapter
import com.caffeinatedr4t. tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.models.User
import kotlinx.coroutines.launch

/**
 * Fragment untuk Manajemen Anggota (Verifikasi Warga dan Hapus) oleh Admin.
 * Menampilkan daftar anggota aktif dan memungkinkan admin untuk memverifikasi/menghapus anggota.
 */
class MemberManagementFragment :  Fragment() {

    // RecyclerView untuk menampilkan daftar anggota
    private lateinit var recyclerView: RecyclerView
    // Adapter untuk mengelola data anggota
    private lateinit var memberAdapter: MemberAdapter
    // TextView untuk menampilkan judul daftar dan jumlah anggota
    private lateinit var tvListTitle:  TextView
    // Daftar anggota yang akan ditampilkan
    private val memberList = mutableListOf<User>()

    /**
     * Membuat dan mengembalikan hierarki tampilan fragmen.
     * Menggunakan layout `fragment_admin_member_list`.
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

        // Inisialisasi View
        recyclerView = view.findViewById(R.id.recyclerViewMembers)
        tvListTitle = view.findViewById(R.id.tvListTitle)

        setupRecyclerView()
        loadMembers()
    }

    /**
     * Menyiapkan RecyclerView dengan adapter dan callback untuk aksi verifikasi/hapus.
     */
    private fun setupRecyclerView() {
        // Inisialisasi Adapter dengan callback untuk Verifikasi (Toggle) dan Hapus
        memberAdapter = MemberAdapter(memberList,
            onVerifyToggle = { user -> handleVerifyToggle(user) }, // Callback untuk Verifikasi/Batal Verifikasi
            onRemove = { user -> handleRemove(user) } // Callback untuk Hapus Anggota
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = memberAdapter
        }
    }

    /**
     * Memuat daftar semua anggota dari BookRepository dan memperbarui adapter.
     * Juga memperbarui judul daftar dengan jumlah anggota.
     */
    private fun loadMembers() {
        // [PERBAIKAN] Bungkus pemanggilan suspend function dengan lifecycleScope.launch
        lifecycleScope.launch {
            // Karena ini di dalam coroutine, UI tidak akan macet saat menunggu data
            val allMembers = BookRepository.getAllMembers()

            memberAdapter.updateData(allMembers)

            // Update Title dengan jumlah anggota
            tvListTitle.text = "Daftar Anggota Aktif (${allMembers.size} Pengguna)"
        }
    }

    /**
     * Menangani logika mengubah status verifikasi (Verified/Unverified) anggota (Verifikasi Warga).
     * @param user Objek User yang status verifikasinya akan diubah.
     */
    private fun handleVerifyToggle(user: User) {
        // ✅ FIX: Handle nullable user. id
        val userId = user.id
        if (userId == null) {
            Toast.makeText(context, "Error: User ID tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // Gunakan coroutine scope fragment
        lifecycleScope.launch {
            // Panggil Repository (kirim ID dan status saat ini)
            val success = BookRepository.toggleVerificationStatus(userId, user.isVerified)

            if (success) {
                val statusMsg = if (!user. isVerified) "Diverifikasi" else "Dibatalkan verifikasi"
                Toast.makeText(context, "Sukses: ${user.fullName} $statusMsg", Toast.LENGTH_SHORT).show()
                loadMembers() // Refresh list
            } else {
                Toast. makeText(context, "Gagal mengubah status.", Toast. LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Menangani logika penghapusan anggota permanen dari sistem.
     * @param user Objek User yang akan dihapus.
     */
    private fun handleRemove(user: User) {
        // ✅ FIX:  Handle nullable user.id
        val userId = user.id
        if (userId == null) {
            Toast.makeText(context, "Error: User ID tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val success = BookRepository.deleteMember(userId)
            if (success) {
                Toast.makeText(context, "Anggota berhasil dihapus.", Toast.LENGTH_SHORT).show()
                loadMembers()
            } else {
                Toast. makeText(context, "Gagal menghapus anggota.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}