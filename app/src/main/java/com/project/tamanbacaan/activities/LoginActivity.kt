package com.caffeinatedr4t.tamanbacaan.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.caffeinatedr4t.tamanbacaan.R

/**
 * Activity yang berfungsi sebagai halaman login untuk pengguna dan admin.
 * Halaman ini menangani tiga skenario login:
 * 1. Login sebagai Admin (Pengelola).
 * 2. Login sebagai pengguna yang baru saja mendaftar (data diterima dari RegisterActivity).
 * 3. Login sebagai pengguna default (dummy account).
 */
class LoginActivity : AppCompatActivity() {
    // Variabel untuk menyimpan data pengguna yang dikirim dari RegisterActivity.
    // Variabel ini akan bernilai null jika activity tidak dibuka dari alur registrasi.
    private var registeredName: String? = null
    private var registeredEmail: String? = null
    private var registeredPassword: String? = null
    private var registeredNik: String? = null
    private var registeredAddress: String? = null

    /**
     * Fungsi yang dipanggil saat Activity pertama kali dibuat.
     * Metode ini menginisialisasi UI, mengambil data dari intent, dan mengatur listener untuk tombol.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inisialisasi komponen UI dari layout XML.
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val tvRegister: TextView = findViewById(R.id.tvRegister)

        // Mengambil data kredensial yang mungkin dikirim dari RegisterActivity.
        // Jika pengguna baru saja mendaftar, data mereka akan diterima di sini.
        registeredName = intent.getStringExtra("REGISTERED_NAME")
        registeredEmail = intent.getStringExtra("REGISTERED_EMAIL")
        registeredPassword = intent.getStringExtra("REGISTERED_PASSWORD")
        registeredNik = intent.getStringExtra("REGISTERED_NIK")
        registeredAddress = intent.getStringExtra("REGISTERED_ADDRESS")

        // Mengatur aksi yang terjadi ketika tombol Login ditekan.
        btnLogin.setOnClickListener {
            // Mengambil input email dan password dari pengguna.
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            // Menggunakan `when` untuk memeriksa beberapa kondisi login secara berurutan.
            when {
                // Kondisi 1: Login sebagai Admin/Pengelola.
                email == "admin@tbm.com" && password == "admin123" -> {
                    Toast.makeText(this, "Login Pengelola Berhasil!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminActivity::class.java))
                    finish() // Menutup LoginActivity agar tidak bisa kembali dengan tombol back.
                }

                // Kondisi 2: Login sebagai pengguna yang baru mendaftar.
                // Memeriksa apakah input cocok dengan data dari RegisterActivity.
                email == registeredEmail && password == registeredPassword -> {
                    Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                    // Membuat intent untuk pindah ke MainActivity.
                    val intent = Intent(this, MainActivity::class.java).apply {
                        // Menyertakan data pengguna untuk ditampilkan di halaman utama.
                        putExtra("USER_NAME", registeredName)
                        putExtra("USER_EMAIL", registeredEmail)
                        putExtra("USER_NIK", registeredNik)
                        putExtra("USER_ADDRESS", registeredAddress)
                    }
                    startActivity(intent)
                    finish() // Menutup LoginActivity.
                }

                // Kondisi 3: Login menggunakan akun default (dummy account untuk testing).
                email == "user@test.com" && password == "123456" -> {
                    Toast.makeText(this, "Login Berhasil (Akun Default)!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java).apply {
                        // Mengirim data dummy ke MainActivity.
                        putExtra("USER_NAME", "User Test")
                        putExtra("USER_EMAIL", "user@test.com")
                        putExtra("USER_NIK", "1234567890123456")
                        putExtra("USER_ADDRESS", "Jl. Contoh Raya No. 123")
                    }
                    startActivity(intent)
                    finish() // Menutup LoginActivity.
                }

                // Kondisi default: Jika tidak ada kredensial yang cocok.
                else -> {
                    Toast.makeText(this, "Email atau Password salah", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Mengatur aksi ketika teks "Register" ditekan.
        tvRegister.setOnClickListener {
            // Memulai RegisterActivity.
            startActivity(Intent(this, RegisterActivity::class.java))
            finish() // Menutup LoginActivity agar alur menjadi lebih rapi.
        }
    }
}
