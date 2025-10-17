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

/**
 * Fragment untuk Manajemen Anggota (Verifikasi Warga dan Hapus) oleh Admin.
 * Menampilkan daftar anggota aktif dan memungkinkan admin untuk memverifikasi/menghapus anggota.
 */
class MemberManagementFragment : Fragment() {

    // RecyclerView untuk menampilkan daftar anggota
    private lateinit var recyclerView: RecyclerView
    // Adapter untuk mengelola data anggota
    private lateinit var memberAdapter: MemberAdapter
    // TextView untuk menampilkan judul daftar dan jumlah anggota
    private lateinit var tvListTitle: TextView
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
        val allMembers = BookRepository.getAllMembers()
        memberAdapter.updateData(allMembers)

        // Update Title dengan jumlah anggota
        tvListTitle.text = "Daftar Anggota Aktif (${allMembers.size} Pengguna)"
    }

    /**
     * Menangani logika mengubah status verifikasi (Verified/Unverified) anggota (Verifikasi Warga).
     * @param user Objek User yang status verifikasinya akan diubah.
     */
    private fun handleVerifyToggle(user: User) {
        val newStatus = !user.isVerified

        // Panggil Repository untuk mengubah status verifikasi
        if (BookRepository.toggleVerificationStatus(user.id)) {
            val action = if (newStatus) "Diverifikasi" else "Dibatalkan verifikasi"
            Toast.makeText(context, "${user.fullName} telah ${action} sebagai warga RT/RW.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Gagal mengubah status verifikasi.", Toast.LENGTH_SHORT).show()
        }
        loadMembers() // Refresh list untuk menampilkan perubahan status
    }

    /**
     * Menangani logika penghapusan anggota permanen dari sistem.
     * @param user Objek User yang akan dihapus.
     */
    private fun handleRemove(user: User) {
        if (BookRepository.deleteMember(user.id)) {
            Toast.makeText(context, "Anggota ${user.fullName} berhasil dihapus permanen.", Toast.LENGTH_LONG).show()
            loadMembers() // Refresh list
        } else {
            Toast.makeText(context, "Gagal menghapus anggota.", Toast.LENGTH_SHORT).show()
        }
    }
}