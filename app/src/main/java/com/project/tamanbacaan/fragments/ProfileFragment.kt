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
import com.caffeinatedr4t.tamanbacaan.activities.MainActivity

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val userName = view.findViewById<TextView>(R.id.userName)
        val userEmail = view.findViewById<TextView>(R.id.userEmail)
        val userNik = view.findViewById<TextView>(R.id.userNik)
        val userAddress = view.findViewById<TextView>(R.id.userAddress)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // 🔹 Ambil data user dari arguments
        userName.text = arguments?.getString("USER_NAME") ?: "Nama Tidak Diketahui"
        userEmail.text = arguments?.getString("USER_EMAIL") ?: "Email Tidak Diketahui"
        userNik.text = "NIK: " + (arguments?.getString("USER_NIK") ?: "NIK Tidak Diketahui")
        userAddress.text = "Alamat: " + (arguments?.getString("USER_ADDRESS") ?: "Alamat Tidak Diketahui")

        // 🔹 Tombol Logout dengan dialog konfirmasi
        btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya") { _, _ ->
                    (requireActivity() as? MainActivity)?.showLogoutConfirmation()
                }
                .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }

        return view
    }
}
