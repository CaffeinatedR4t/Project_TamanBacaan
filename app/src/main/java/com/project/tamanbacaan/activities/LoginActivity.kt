package com.caffeinatedr4t.tamanbacaan.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.api.ApiConfig
import com.caffeinatedr4t.tamanbacaan.api.model.LoginRequest
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.utils.SharedPrefsManager
import kotlinx.coroutines.launch

/**
 * Activity untuk login pengguna dan admin dengan koneksi ke Backend API.
 * Mendukung login untuk role MEMBER dan ADMIN.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPrefsManager: SharedPrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize UI components
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
        progressBar = findViewById(R.id.progressBar)

        // Initialize SharedPreferences
        sharedPrefsManager = SharedPrefsManager(this)

        // --- CEK AUTO LOGIN (DIPERBAIKI) ---
        if (sharedPrefsManager.isLoggedIn()) {
            val user = sharedPrefsManager.getUser()

            // [FIX CRASH] Tambahkan '&& !user.id.isNullOrEmpty()'
            // Ini mencegah crash jika data ID di HP ternyata kosong/null
            if (user != null && user.isVerified && !user.id.isNullOrEmpty()) {

                // Set ID ke Repository agar transaksi jalan
                BookRepository.setUserId(user.id)

                navigateBasedOnRole(user.role)
                return
            } else {
                // Jika data user tidak valid atau ID null, hapus sesi agar login ulang
                sharedPrefsManager.clearSession()
            }
        }
        // -----------------------------------

        // Login button click listener
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            // Validate input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call login API
            loginUser(email, password)
        }

        // Register text click listener
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * Function to call login API
     */
    private fun loginUser(email: String, password: String) {
        setLoading(true)

        val loginRequest = LoginRequest(email, password)

        lifecycleScope.launch {
            try {
                val apiService = ApiConfig.getApiService()
                val response = apiService.login(loginRequest)

                setLoading(false)

                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    if (loginResponse == null) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login gagal: response kosong",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    // 🔒 CEK VERIFIKASI USER
                    if (!loginResponse.user.isVerified) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Akun Anda belum diverifikasi oleh admin.\nSilakan tunggu proses verifikasi.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@launch
                    }

                    // ✅ LOGIN SAH
                    Toast.makeText(
                        this@LoginActivity,
                        "Login Berhasil! Selamat datang ${loginResponse.user.fullName}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Simpan sesi user
                    sharedPrefsManager.saveUserSession(
                        loginResponse.user,
                        loginResponse.token
                    )

                    // [BARU] Simpan ID User ke Repository (Safe Call)
                    val userId = loginResponse.user.id
                    if (!userId.isNullOrEmpty()) {
                        BookRepository.setUserId(userId)
                    }

                    navigateBasedOnRole(loginResponse.user.role)

                } else {
                    // ❌ HTTP ERROR (401, 403, dll)
                    Toast.makeText(
                        this@LoginActivity,
                        "Gagal login: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                setLoading(false)
                Toast.makeText(
                    this@LoginActivity,
                    "Error: ${e.message}\nPastikan backend sudah berjalan!",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }
    }

    /**
     * Navigate to appropriate screen based on user role
     */
    private fun navigateBasedOnRole(role: String?) {
        val intent = when (role) {
            "ADMIN" -> Intent(this, AdminActivity::class.java)
            "MEMBER" -> Intent(this, MainActivity::class.java)
            else -> {
                Toast.makeText(this, "Role tidak valid", Toast.LENGTH_SHORT).show()
                return
            }
        }
        startActivity(intent)
        finish()
    }

    /**
     * Show/hide loading indicator
     */
    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            btnLogin.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            btnLogin.isEnabled = true
        }
    }
}