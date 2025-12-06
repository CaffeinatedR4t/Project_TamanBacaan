package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.activities.auth.LoginActivity
import com.caffeinatedr4t.tamanbacaan.utils.DataSeeder

/**
 * Fragment untuk halaman profil Pengelola (Admin).
 * Menampilkan informasi admin dan tombol untuk logout.
 */
class AdminProfileFragment : Fragment() {

    /**
     * Membuat dan mengembalikan hierarki tampilan fragmen.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Menginflate layout XML untuk fragment profil admin
        val view = inflater.inflate(R.layout.fragment_admin_profile, container, false)

        // Variabel untuk elemen UI
        val adminName = view.findViewById<TextView>(R.id.adminName) // TextView untuk Nama Admin
        val adminEmail = view.findViewById<TextView>(R.id.adminEmail) // TextView untuk Email Admin
        val adminRole = view.findViewById<TextView>(R.id.adminRole) // TextView untuk Role Admin
        val btnSeedData = view.findViewById<Button>(R.id.btnSeedData) // Tombol untuk Seed Data Testing
        val btnLogout = view.findViewById<Button>(R.id.btnAdminLogout) // Tombol untuk Logout Admin

        // Mengisi data Admin (Data Dummy: admin@tbm.com/admin123)
        adminName.text = "Kevin Gunawan"
        adminEmail.text = "admin@tbm.com"
        adminRole.text = "Pengelola Taman Bacaan"

        // Listener untuk tombol Seed Data
        btnSeedData.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Seed Test Data")
                .setMessage("Tambah data testing (buku, user, event) ke database MongoDB?\n\nPerhatian: Proses ini akan menambahkan 15 buku, 5 user, dan 5 event sample.")
                .setPositiveButton("Ya") { _, _ ->
                    // Tampilkan loading toast
                    Toast.makeText(requireContext(), "Memproses... Mohon tunggu", Toast.LENGTH_SHORT).show()
                    
                    // Panggil DataSeeder
                    DataSeeder.seedAllData(requireContext()) { result ->
                        // Tampilkan hasil dalam dialog
                        AlertDialog.Builder(requireContext())
                            .setTitle("Hasil Seeding Data")
                            .setMessage(result)
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // Listener untuk tombol Logout
        btnLogout.setOnClickListener {
            // Tampilkan dialog konfirmasi sebelum logout
            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar dari akun admin?")
                .setPositiveButton("Ya") { _, _ ->
                    // Navigasi kembali ke LoginActivity dan menghapus riwayat Activity
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        return view
    }
}