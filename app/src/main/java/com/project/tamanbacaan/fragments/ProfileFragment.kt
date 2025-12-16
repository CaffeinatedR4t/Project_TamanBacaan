package com.caffeinatedr4t.tamanbacaan.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.activities.MainActivity
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.data.UpdateProfileRequest
import com.caffeinatedr4t.tamanbacaan.models.User
import com.caffeinatedr4t.tamanbacaan.utils.SharedPrefsManager
import com.caffeinatedr4t.tamanbacaan.viewmodels.profile.ProfileViewModel
import com.google.android.material.textfield.TextInputEditText
import com.project.tamanbacaan.viewmodels.profile.ProfileState
import com.project.tamanbacaan.viewmodels.profile.ProfileViewModelFactory
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userNik: TextView
    private lateinit var userAddress: TextView
    private lateinit var sharedPrefsManager: SharedPrefsManager
    private lateinit var viewModel: ProfileViewModel
    private lateinit var btnEditProfile: Button
    private lateinit var progressBar: ProgressBar
    private var currentUser: User? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val factory = ProfileViewModelFactory(
            BookRepository,
            SharedPrefsManager(requireContext())
        )

        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        // Init View
        userName = view.findViewById(R.id.userName)
        userEmail = view.findViewById(R.id.userEmail)
        userNik = view.findViewById(R.id.userNik)
        userAddress = view.findViewById(R.id.userAddress)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        progressBar = view.findViewById(R.id.progressProfile)

        // Init Prefs
        sharedPrefsManager = SharedPrefsManager(requireContext())

        // [BARU] Load Data Profil dari Server
        observeProfile()
        viewModel.loadProfile()

        btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        return view
    }

    private fun observeProfile() {
        viewModel.profileState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ProfileState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }

                is ProfileState.Success -> {
                    progressBar.visibility = View.GONE
                    val user = state.user
                    currentUser = user
                    userName.text = user.fullName
                    userEmail.text = user.email
                    userNik.text = "NIK: ${user.nik}"

                    userAddress.text =
                        "Alamat: ${user.addressRtRw}, ${user.addressKelurahan}, ${user.addressKecamatan}"

                    Log.d("PROFILE", "RT/RW: ${user.addressRtRw}")
                    Log.d("PROFILE", "Kelurahan: ${user.addressKelurahan}")
                    Log.d("PROFILE", "Kecamatan: ${user.addressKecamatan}")

                }

                is ProfileState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_profile, null)

        val etName = dialogView.findViewById<TextInputEditText>(R.id.etEditName)
        val etEmail = dialogView.findViewById<TextInputEditText>(R.id.etEditEmail)
        val etRtRw = dialogView.findViewById<TextInputEditText>(R.id.etEditRtRw)
        val etKelurahan = dialogView.findViewById<TextInputEditText>(R.id.etEditKelurahan)
        val etKecamatan = dialogView.findViewById<TextInputEditText>(R.id.etEditKecamatan)

        // 🔹 Prefill dari TextView (AMAN)
        etName.setText(userName.text.toString())
        etEmail.setText(userEmail.text.toString())
        currentUser?.let { user ->
            etRtRw.setText(user.addressRtRw ?: "")
            etKelurahan.setText(user.addressKelurahan ?: "")
            etKecamatan.setText(user.addressKecamatan ?: "")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Profil")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->

                val newName = etName.text.toString().trim()
                val newEmail = etEmail.text.toString().trim()

                if (newName.isEmpty() || newEmail.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Nama dan Email wajib diisi",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    Toast.makeText(
                        requireContext(),
                        "Format email tidak valid",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                // 🔥 MVVM CALL (TANPA coroutine)
                viewModel.updateProfile(
                    UpdateProfileRequest(
                        fullName = newName,
                        email = newEmail,
                        addressRtRw = etRtRw.text.toString(),
                        addressKelurahan = etKelurahan.text.toString(),
                        addressKecamatan = etKecamatan.text.toString()
                    )
                )

                Toast.makeText(
                    requireContext(),
                    "Memperbarui profil...",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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