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

        // 🔹 Sembunyikan field nama orang tua jika bukan anak
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

            // 🔸 Validasi dasar
            if (fullName.isEmpty() || nik.length != 16 || email.isEmpty() || password.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua data dengan benar (NIK harus 16 digit).", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (isChild && parentName.isNullOrEmpty()) {
                Toast.makeText(this, "Nama Orang Tua wajib diisi untuk anak.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // SIMULASI REGISTRASI dengan NIK KTP / NIK Orang Tua
            // Di sini Anda akan memanggil Retrofit ApiService.register(RegisterRequest)
            Toast.makeText(this, "Pendaftaran sedang diproses dengan NIK: $nik. Mohon tunggu verifikasi oleh Pengelola TBM.", Toast.LENGTH_LONG).show()

            // 🔸 Kirim data user baru ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java).apply {
                putExtra("REGISTERED_NAME", fullName)
                putExtra("REGISTERED_EMAIL", email)
                putExtra("REGISTERED_PASSWORD", password)
                putExtra("REGISTERED_NIK", nik)
                putExtra("REGISTERED_ADDRESS", address)
            }
            startActivity(intent)
            finish()
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}