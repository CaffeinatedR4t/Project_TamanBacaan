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
import com.caffeinatedr4t.tamanbacaan.utils.SharedPrefsManager
import kotlinx.coroutines.launch

/**
 * Activity untuk login pengguna dan admin dengan koneksi ke Backend API.
 * Mendukung login untuk role MEMBER dan ADMIN.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail:  EditText
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
        tvRegister = findViewById(R.id. tvRegister)
        // Add ProgressBar to your layout XML:  <ProgressBar android:id="@+id/progressBar" ...  />
        progressBar = findViewById(R.id.progressBar) // You need to add this to your XML

        // Initialize SharedPreferences
        sharedPrefsManager = SharedPrefsManager(this)

        // Check if user is already logged in
        if (sharedPrefsManager.isLoggedIn()) {
            navigateBasedOnRole(sharedPrefsManager.getUserRole())
            return
        }

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
            startActivity(Intent(this, RegisterActivity:: class.java))
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

                    // [PERBAIKAN] Gunakan ?.user karena loginResponse bisa null
                    val user = loginResponse?.user

                    if (loginResponse?.success == true && user != null) {

                        // [LOGIKA BARU] Cek Verifikasi
                        if (!user.isVerified && user.role == "MEMBER") {
                            Toast.makeText(
                                this@LoginActivity,
                                "Akun Anda belum diverifikasi oleh Admin. Silakan hubungi pengelola TBM.",
                                Toast.LENGTH_LONG
                            ).show()
                            return@launch
                        }

                        Toast.makeText(
                            this@LoginActivity,
                            "Login Berhasil! Selamat datang ${user.fullName}",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Simpan sesi
                        sharedPrefsManager.saveUserSession(
                            user,
                            loginResponse.token ?: ""
                        )

                        navigateBasedOnRole(user.role)

                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            loginResponse?.message ?: "Email atau Password salah",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
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
            progressBar.visibility = View. VISIBLE
            btnLogin.isEnabled = false
        } else {
            progressBar. visibility = View.GONE
            btnLogin.isEnabled = true
        }
    }
}