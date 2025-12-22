package com.caffeinatedr4t.tamanbacaan.fragments.admin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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
import com.caffeinatedr4t.tamanbacaan.activities.LoginActivity
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.data.UpdateProfileRequest
import com.caffeinatedr4t.tamanbacaan.utils.SharedPrefsManager
import com.caffeinatedr4t.tamanbacaan.viewmodels.profile.ProfileViewModel
import com.google.android.material.textfield.TextInputEditText
import com.project.tamanbacaan.viewmodels.profile.ProfileState
import com.project.tamanbacaan.viewmodels.profile.ProfileViewModelFactory
import kotlinx.coroutines.launch

class AdminProfileFragment : Fragment() {

    private lateinit var adminName: TextView
    private lateinit var adminEmail: TextView
    private lateinit var adminRole: TextView

    // [BARU] Variabel untuk Statistik Cepat
    private lateinit var tvTotalBooks: TextView
    private lateinit var tvTotalMembers: TextView
    private lateinit var tvActiveLoans: TextView

    private lateinit var sharedPrefsManager: SharedPrefsManager
    private lateinit var btnEditProfile: Button
    private lateinit var viewModel: ProfileViewModel
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_profile, container, false)
        val factory = ProfileViewModelFactory(
            BookRepository,
            SharedPrefsManager(requireContext())
        )

        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        // Init View Profil
        adminName = view.findViewById(R.id.adminName)
        adminEmail = view.findViewById(R.id.adminEmail)
        adminRole = view.findViewById(R.id.adminRole)
        btnEditProfile = view.findViewById(R.id.btnEditAdminProfile)
        progressBar = view.findViewById(R.id.progressProfile)

        // [BARU] Init View Statistik
        tvTotalBooks = view.findViewById(R.id.tvTotalBooks)
        tvTotalMembers = view.findViewById(R.id.tvTotalMembers)
        tvActiveLoans = view.findViewById(R.id.tvActiveLoans)

        val btnLogout = view.findViewById<Button>(R.id.btnAdminLogout)

        sharedPrefsManager = SharedPrefsManager(requireContext())

        // Load Data Profil Admin
        observeProfile()
        viewModel.loadProfile()

        // [BARU] Load Data Statistik Database
        loadStatistics()

        btnEditProfile.setOnClickListener {
            showEditAdminProfileDialog()
        }

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        return view
    }

    /**
     * [BARU] Fungsi untuk mengambil data statistik dari database (via Repository)
     * Mengambil Total Buku, Total Anggota, dan Jumlah Peminjaman Aktif.
     */
    private fun loadStatistics() {
        lifecycleScope.launch {
            try {
                // 1. Ambil data buku untuk total koleksi
                val allBooks = BookRepository.getAllBooks()

                // 2. Ambil data anggota
                val allMembers = BookRepository.getAllMembers()

                // Hitung statistik dasar
                val totalBooks = allBooks.size
                val totalMembers = allMembers.size

                // 3. [UPDATED] Ambil jumlah buku yang sedang dipinjam langsung dari data Transaksi
                // Menggunakan fungsi yang sama dengan ReportFragment agar data konsisten
                val activeLoans = BookRepository.getBorrowedBooksCount()

                // Update UI (Pastikan fragment masih terpasang)
                if (isAdded) {
                    tvTotalBooks.text = totalBooks.toString()
                    tvTotalMembers.text = totalMembers.toString()
                    tvActiveLoans.text = activeLoans.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Jika gagal, biarkan angka default (0)
            }
        }
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
                    adminName.text = user.fullName
                    adminEmail.text = user.email
                    // Menampilkan role dengan format rapi
                    adminRole.text = "Role: ${user.role} (Verified)"
                }

                is ProfileState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }

    private fun showEditAdminProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_admin_profile, null)

        val etEditAdminName = dialogView.findViewById<TextInputEditText>(R.id.etEditAdminName)
        val etEditAdminEmail = dialogView.findViewById<TextInputEditText>(R.id.etEditAdminEmail)

        // Pre-fill data saat ini
        etEditAdminName.setText(adminName.text.toString())
        etEditAdminEmail.setText(adminEmail.text.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile Admin")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val newName = etEditAdminName.text.toString().trim()
                val newEmail = etEditAdminEmail.text.toString().trim()

                if (newName.isEmpty() || newEmail.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Nama dan Email harus diisi!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    Toast.makeText(
                        requireContext(),
                        "Format email tidak valid!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                viewModel.updateProfile(
                    UpdateProfileRequest(
                        fullName = newName,
                        email = newEmail
                    )
                )

                Toast.makeText(
                    requireContext(),
                    "Memperbarui profile...",
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