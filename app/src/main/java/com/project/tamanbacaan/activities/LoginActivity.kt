package com.caffeinatedr4t.tamanbacaan.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.caffeinatedr4t.tamanbacaan.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val tvRegister: TextView = findViewById(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            // Simulasi Dual Role Login
            when {
                // ADMIN LOGIN
                email == "admin@tbm.com" && password == "admin123" -> {
                    Toast.makeText(this, "Login Pengelola Berhasil!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AdminActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                // MEMBER LOGIN
                email == "user@test.com" && password == "123456" -> {
                    Toast.makeText(this, "Login Anggota berhasil!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    Toast.makeText(this, "Email atau Password salah", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Arahkan ke Halaman Registrasi yang baru
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}