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

            // FIX: Langsung register dan aktifkan pengguna
            val newUser = BookRepository.registerNewMember(
                fullName, nik, email, address, isChild, parentName
            )

            if (newUser != null) {
                Toast.makeText(this, "Pendaftaran Berhasil! Silakan Login dengan Email Anda.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Gagal: Email atau NIK sudah terdaftar.", Toast.LENGTH_SHORT).show()
            }

            // Kembali ke halaman login
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