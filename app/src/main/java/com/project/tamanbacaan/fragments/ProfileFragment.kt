package com.caffeinatedr4t.tamanbacaan.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.activities.MainActivity
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.utils.SharedPrefsManager
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userNik: TextView
    private lateinit var userAddress: TextView
    private lateinit var sharedPrefsManager: SharedPrefsManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Init View
        userName = view.findViewById(R.id.userName)
        userEmail = view.findViewById(R.id.userEmail)
        userNik = view.findViewById(R.id.userNik)
        userAddress = view.findViewById(R.id.userAddress)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // Init Prefs
        sharedPrefsManager = SharedPrefsManager(requireContext())

        // [BARU] Load Data Profil dari Server
        loadUserProfile()

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        return view
    }

    private fun loadUserProfile() {
        val token = sharedPrefsManager.getUserToken()

        if (!token.isNullOrEmpty()) {
            lifecycleScope.launch {
                // Panggil Repository
                val user = BookRepository.getUserProfile(token)

                if (user != null) {
                    // Tampilkan data asli dari database
                    userName.text = user.fullName
                    userEmail.text = user.email
                    userNik.text = "NIK: ${user.nik}"

                    val fullAddress = "${user.addressRtRw}, ${user.addressKelurahan}, ${user.addressKecamatan}"
                    userAddress.text = "Alamat: $fullAddress"
                } else {
                    Toast.makeText(context, "Gagal memuat profil terbaru", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLogoutDialog() {
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
}