package com.caffeinatedr4t.tamanbacaan.activities

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
import com.caffeinatedr4t.tamanbacaan.data.BookRepository // Import Repository
import com.caffeinatedr4t.tamanbacaan.data.RegistrationRequest // Import Model

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etFullName: EditText = findViewById(R.id.etFullName)
        val etNik: EditText = findViewById(R.id.etNik)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val etAddressRtRw: EditText = findViewById(R.id.etAddressRtRw)
        val cbIsChild: CheckBox = findViewById(R.id.cbIsChild)
        val etParentName: EditText = findViewById(R.id.etParentName)
        val btnRegister: Button = findViewById(R.id.btnRegister)
        val tvLogin: TextView = findViewById(R.id.tvLogin)

        // Tampilkan/Sembunyikan input nama orang tua berdasarkan status anak
        cbIsChild.setOnCheckedChangeListener { _, isChecked ->
            etParentName.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) etParentName.text.clear()
        }

        btnRegister.setOnClickListener {
            val fullName = etFullName.text.toString()
            val nik = etNik.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val address = etAddressRtRw.text.toString()
            val isChild = cbIsChild.isChecked
            val parentName = if (isChild) etParentName.text.toString() else null

            // Validasi Dasar
            if (fullName.isEmpty() || nik.length != 16 || email.isEmpty() || password.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi semua data dengan benar. Pastikan NIK 16 digit.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (isChild && parentName.isNullOrEmpty()) {
                Toast.makeText(this, "Nama Orang Tua wajib diisi untuk pendaftaran Anak.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // SIMULASI PANGGIL REPOSITORY UNTUK MENYIMPAN REQUEST
            val request = RegistrationRequest(
                requestId = "", // ID akan diisi oleh Repository
                fullName = fullName,
                nik = nik,
                isChild = isChild,
                parentName = parentName,
                addressRtRw = address,
                requestDate = "Hari Ini"
            )

            if (BookRepository.addRegistrationRequest(request)) {
                Toast.makeText(this, "Pendaftaran berhasil dikirim. Menunggu verifikasi oleh Pengelola TBM.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Gagal mengirim pendaftaran. Coba lagi.", Toast.LENGTH_SHORT).show()
            }

            // Setelah pendaftaran berhasil/dikirim, kembali ke halaman login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}