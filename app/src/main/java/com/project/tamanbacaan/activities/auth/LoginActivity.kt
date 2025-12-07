package com.caffeinatedr4t.tamanbacaan.activities.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.caffeinatedr4t.tamanbacaan.api.ApiConfig
import com.caffeinatedr4t.tamanbacaan.api.model.LoginRequest
import com.caffeinatedr4t.tamanbacaan.api.model.LoginResponse
import com.caffeinatedr4t.tamanbacaan.databinding.ActivityLoginBinding
import com.caffeinatedr4t.tamanbacaan.activities.main.MainActivity
import com.caffeinatedr4t.tamanbacaan.utils.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPrefs: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefs = SharedPreferencesHelper(this)

        // Check if already logged in
        if (sharedPrefs.isLoggedIn()) {
            navigateToMain()
            return
        }

        setupListeners()
    }

    private fun setupListeners() {
        // Login button click
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text. toString().trim()
            val password = binding.etPassword.text. toString().trim()

            if (validateInput(email, password)) {
                performLogin(email, password)
            }
        }

        // Register link click
        binding.tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            Toast. makeText(this, "Email harus diisi", Toast.LENGTH_SHORT).show()
            binding. etEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show()
            binding. etEmail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password harus diisi", Toast.LENGTH_SHORT).show()
            binding.etPassword. requestFocus()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
            binding.etPassword.requestFocus()
            return false
        }

        return true
    }

    private fun performLogin(email: String, password: String) {
        showLoading(true)

        val request = LoginRequest(email, password)
        val apiService = ApiConfig.getApiService()

        apiService.login(request). enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                showLoading(false)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        // Save login data to SharedPreferences
                        sharedPrefs.saveLoginData(
                            token = loginResponse.token,
                            userId = loginResponse. user.id,
                            userName = loginResponse.user.fullName,
                            email = loginResponse.user.email,
                            role = loginResponse.user.role
                        )

                        Toast.makeText(
                            this@LoginActivity,
                            "Login berhasil!  Selamat datang ${loginResponse.user.fullName}",
                            Toast.LENGTH_SHORT
                        ).show()

                        navigateToMain()
                    }
                } else {
                    val errorMsg = when (response. code()) {
                        401 -> "Email atau password salah"
                        404 -> "Akun tidak ditemukan"
                        403 -> "Akun Anda belum diverifikasi oleh admin"
                        else -> "Login gagal. Silakan coba lagi."
                    }
                    Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showLoading(false)
                
                // Enhanced error message based on exception type
                val errorMsg = when {
                    t is java.net.UnknownHostException -> 
                        "Tidak dapat terhubung ke server. Pastikan backend berjalan dan URL sudah benar."
                    t is java.net.SocketTimeoutException -> 
                        "Koneksi timeout. Server mungkin tidak merespons."
                    t is java.net.ConnectException -> 
                        "Gagal terhubung ke server. Periksa apakah backend sudah berjalan."
                    t.message?.contains("Failed to connect") == true -> 
                        "Koneksi gagal. Pastikan backend berjalan dan dapat diakses."
                    else -> "Kesalahan koneksi: ${t.message}"
                }
                
                android.util.Log.e("LoginActivity", "Login failed", t)
                Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View. GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.tvRegisterLink.isEnabled = ! isLoading
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}