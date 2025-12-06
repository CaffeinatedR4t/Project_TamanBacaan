package com.caffeinatedr4t.tamanbacaan.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.activities.main.MainActivity

/**
 * Fragment untuk menampilkan profil pengguna (Anggota TBM).
 * Menyediakan informasi dasar pengguna dan fungsi untuk logout.
 */
class ProfileFragment : Fragment() {

    /**
     * Dipanggil untuk membuat dan mengembalikan hierarki tampilan yang terkait dengan fragmen.
     * @param inflater Digunakan untuk meng-inflate layout.
     * @param container Grup tampilan induk tempat fragmen akan dilampirkan.
     * @param savedInstanceState Data yang sebelumnya disimpan (jika ada).
     * @return Tampilan (View) root fragmen.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Menginflate layout XML untuk fragment profil
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Variabel untuk elemen UI (TextViews) di layout fragment_profile.xml
        val userName = view.findViewById<TextView>(R.id.userName) // TextView untuk Nama Pengguna
        val userEmail = view.findViewById<TextView>(R.id.userEmail) // TextView untuk Email Pengguna
        val userNik = view.findViewById<TextView>(R.id.userNik) // TextView untuk NIK Pengguna
        val userAddress = view.findViewById<TextView>(R.id.userAddress) // TextView untuk Alamat Pengguna
        val btnLogout = view.findViewById<Button>(R.id.btnLogout) // Tombol untuk Logout

        // Ambil data user dari arguments (dikirim dari LoginActivity/MainActivity) dan set ke TextView
        userName.text = arguments?.getString("USER_NAME") ?: "Nama Tidak Diketahui"
        userEmail.text = arguments?.getString("USER_EMAIL") ?: "Email Tidak Diketahui"
        userNik.text = "NIK: " + (arguments?.getString("USER_NIK") ?: "NIK Tidak Diketahui")
        userAddress.text = "Alamat: " + (arguments?.getString("USER_ADDRESS") ?: "Alamat Tidak Diketahui")

        // Listener untuk Tombol Logout dengan dialog konfirmasi
        btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya") { _, _ ->
                    // Memanggil fungsi showLogoutConfirmation() di MainActivity untuk menyelesaikan proses logout
                    (requireActivity() as? MainActivity)?.showLogoutConfirmation()
                }
                .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }

        return view
    }
}