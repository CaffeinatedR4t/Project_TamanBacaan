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
import androidx.lifecycle.lifecycleScope
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.activities.LoginActivity
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.utils.SharedPrefsManager
import kotlinx.coroutines.launch

class AdminProfileFragment : Fragment() {

    private lateinit var adminName: TextView
    private lateinit var adminEmail: TextView
    private lateinit var adminRole: TextView
    private lateinit var sharedPrefsManager: SharedPrefsManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_profile, container, false)

        adminName = view.findViewById(R.id.adminName)
        adminEmail = view.findViewById(R.id.adminEmail)
        adminRole = view.findViewById(R.id.adminRole)
        val btnLogout = view.findViewById<Button>(R.id.btnAdminLogout)

        sharedPrefsManager = SharedPrefsManager(requireContext())

        // [BARU] Load Data Admin dari Server
        loadAdminProfile()

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        return view
    }

    private fun loadAdminProfile() {
        val token = sharedPrefsManager.getUserToken()

        if (!token.isNullOrEmpty()) {
            lifecycleScope.launch {
                // Panggil Repository (Endpoint yang sama dengan Member)
                val user = BookRepository.getUserProfile(token)

                if (user != null) {
                    adminName.text = user.fullName
                    adminEmail.text = user.email
                    adminRole.text = "Role: ${user.role} (Verified)"
                } else {
                    Toast.makeText(context, "Gagal memuat profil admin", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar dari akun admin?")
            .setPositiveButton("Ya") { _, _ ->
                sharedPrefsManager.clearSession()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}