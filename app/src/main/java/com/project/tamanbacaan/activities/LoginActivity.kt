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
    private var registeredName: String? = null
    private var registeredEmail: String? = null
    private var registeredPassword: String? = null
    private var registeredNik: String? = null
    private var registeredAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val tvRegister: TextView = findViewById(R.id.tvRegister)

        // 🔹 Ambil data yang dikirim dari RegisterActivity
        registeredName = intent.getStringExtra("REGISTERED_NAME")
        registeredEmail = intent.getStringExtra("REGISTERED_EMAIL")
        registeredPassword = intent.getStringExtra("REGISTERED_PASSWORD")
        registeredNik = intent.getStringExtra("REGISTERED_NIK")
        registeredAddress = intent.getStringExtra("REGISTERED_ADDRESS")

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            when {
                email == "admin@tbm.com" && password == "admin123" -> {
                    Toast.makeText(this, "Login Pengelola Berhasil!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminActivity::class.java))
                    finish()
                }

                // 🔹 Cek login user terdaftar dari RegisterActivity
                email == registeredEmail && password == registeredPassword -> {
                    Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("USER_NAME", registeredName)
                        putExtra("USER_EMAIL", registeredEmail)
                        putExtra("USER_NIK", registeredNik)
                        putExtra("USER_ADDRESS", registeredAddress)
                    }
                    startActivity(intent)
                    finish()
                }

                // 🔸 Login Dummy Default
                email == "user@test.com" && password == "123456" -> {
                    Toast.makeText(this, "Login Berhasil (Akun Default)!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("USER_NAME", "User Test")
                        putExtra("USER_EMAIL", "user@test.com")
                        putExtra("USER_NIK", "1234567890123456")
                        putExtra("USER_ADDRESS", "Jl. Contoh Raya No. 123")
                    }
                    startActivity(intent)
                    finish()
                }

                else -> {
                    Toast.makeText(this, "Email atau Password salah", Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}
