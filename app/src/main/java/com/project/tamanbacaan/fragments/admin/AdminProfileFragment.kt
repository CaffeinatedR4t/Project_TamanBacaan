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

class AdminProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_profile, container, false)

        val adminName = view.findViewById<TextView>(R.id.adminName)
        val adminEmail = view.findViewById<TextView>(R.id.adminEmail)
        val adminRole = view.findViewById<TextView>(R.id.adminRole)
        val btnLogout = view.findViewById<Button>(R.id.btnAdminLogout)

        // Dummy Data (bisa diambil dari LoginActivity nanti)
        adminName.text = "Kevin Gunawan"
        adminEmail.text = "admin@tbm.com"
        adminRole.text = "Pengelola Taman Bacaan"

        btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar dari akun admin?")
                .setPositiveButton("Ya") { _, _ ->
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
