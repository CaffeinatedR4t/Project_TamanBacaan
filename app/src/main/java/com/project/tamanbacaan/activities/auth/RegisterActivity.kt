package com.caffeinatedr4t.tamanbacaan.activities.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.caffeinatedr4t.tamanbacaan.R

/**
 * Activity yang berfungsi sebagai halaman pendaftaran untuk anggota baru.
 * Pengguna dapat mendaftar sebagai anggota dewasa atau anggota anak-anak.
 */
class RegisterActivity : AppCompatActivity() {

    /**
     * Fungsi yang dipanggil saat Activity pertama kali dibuat.
     * Bertanggung jawab untuk inisialisasi UI, dan mengatur listener untuk interaksi pengguna.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // --- Inisialisasi Komponen UI dari Layout ---
        val etFullName: EditText = findViewById(R.id.etFullName)
        val etNik: EditText = findViewById(R.id.etNik)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val etAddressRtRw: EditText = findViewById(R.id.etAddressRtRw)
        val cbIsChild: CheckBox = findViewById(R.id.cbIsChild)
        val etParentName: EditText = findViewById(R.id.etParentName)
        val btnRegister: Button = findViewById(R.id.btnRegister)
        val tvLogin: TextView = findViewById(R.id.tvLogin)

        // --- Logika untuk Menampilkan/Menyembunyikan Input Nama Orang Tua ---
        // Mengatur listener pada checkbox 'Anggota Anak-anak'.
        cbIsChild.setOnCheckedChangeListener { _, isChecked ->
            // Jika checkbox dicentang (isChecked == true), tampilkan input nama orang tua.
            // Jika tidak, sembunyikan.
            etParentName.visibility = if (isChecked) View.VISIBLE else View.GONE
            // Jika checkbox tidak dicentang, kosongkan input nama orang tua untuk membersihkan data.
            if (!isChecked) {
                etParentName.text.clear()
            }
        }

        // --- Logika untuk Tombol Pendaftaran ---
        btnRegister.setOnClickListener {
            // Mengambil semua nilai dari input field dan mengubahnya menjadi String.
            val fullName = etFullName.text.toString().trim()
            val nik = etNik.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString() // Password tidak perlu di-trim
            val address = etAddressRtRw.text.toString().trim()
            val isChild = cbIsChild.isChecked
            // Jika mendaftar sebagai anak, ambil nama orang tua. Jika tidak, nilainya null.
            val parentName = if (isChild) etParentName.text.toString().trim() else null

            // --- Validasi Input Pengguna ---
            // Memeriksa apakah field wajib sudah diisi dan NIK memiliki panjang 16 digit.
            if (fullName.isEmpty() || nik.length != 16 || email.isEmpty() || password.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua data dengan benar (NIK harus 16 digit).", Toast.LENGTH_LONG).show()
                return@setOnClickListener // Menghentikan eksekusi jika validasi gagal.
            }
            // Validasi tambahan khusus jika yang mendaftar adalah anak.
            if (isChild && parentName.isNullOrEmpty()) {
                Toast.makeText(this, "Nama Orang Tua wajib diisi untuk anak.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Menghentikan eksekusi.
            }

            // --- Simulasi Proses Pendaftaran ---
            // Di aplikasi nyata, di sinilah Anda akan memanggil API (misalnya dengan Retrofit)
            // untuk mengirim data pendaftaran ke server untuk diverifikasi oleh admin.
            Toast.makeText(this, "Pendaftaran sedang diproses dengan NIK: $nik. Mohon tunggu verifikasi oleh Pengelola TBM.", Toast.LENGTH_LONG).show()

            // --- Mengirim Data ke LoginActivity ---
            // Setelah pendaftaran, data pengguna dikirim ke LoginActivity agar pengguna
            // bisa langsung login setelah akunnya diverifikasi.
            val intent = Intent(this, LoginActivity::class.java).apply {
                putExtra("REGISTERED_NAME", fullName)
                putExtra("REGISTERED_EMAIL", email)
                putExtra("REGISTERED_PASSWORD", password)
                putExtra("REGISTERED_NIK", nik)
                putExtra("REGISTERED_ADDRESS", address)
            }
            startActivity(intent) // Memulai LoginActivity.
            finish() // Menutup RegisterActivity agar pengguna tidak bisa kembali ke halaman ini.
        }

        // --- Logika untuk Teks 'Sudah punya akun? Login' ---
        tvLogin.setOnClickListener {
            // Jika pengguna sudah punya akun, arahkan kembali ke LoginActivity.
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Menutup RegisterActivity.
        }
    }
}