package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.activities.LoginActivity

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
        val btnLogout = view.findViewById<Button>(R.id.btnAdminLogout) // Tombol untuk Logout Admin

        // Mengisi data Admin (Data Dummy: admin@tbm.com/admin123)
        adminName.text = "Kevin Gunawan"
        adminEmail.text = "admin@tbm.com"
        adminRole.text = "Pengelola Taman Bacaan"

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