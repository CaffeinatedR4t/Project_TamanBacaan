// LoginActivity.kt
package com.caffeinatedr4t.tamanbacaan.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.utils.SharedPrefsManager
import com.caffeinatedr4t.tamanbacaan.state.LoginState
import com.caffeinatedr4t.tamanbacaan.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPrefsManager: SharedPrefsManager

    // Inisialisasi ViewModel
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize UI components
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
        progressBar = findViewById(R.id.progressBar)

        sharedPrefsManager = SharedPrefsManager(this)

        // 1. Cek Auto Login (Tetap di sini atau dipindah ke SplashActivity lebih baik)
        checkAutoLogin()

        // 2. Setup Listeners
        setupListeners()

        // 3. Observe ViewModel (Bagian Penting MVVM)
        observeViewModel()
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            // Panggil fungsi login di ViewModel
            viewModel.login(email, password)
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Loading -> {
                    setLoading(true)
                }
                is LoginState.Success -> {
                    setLoading(false)
                    val response = state.data

                    Toast.makeText(this, "Login Berhasil! Selamat datang ${response.user.fullName}", Toast.LENGTH_SHORT).show()

                    // Simpan sesi (Side Effect UI/Storage tetap aman di Activity)
                    sharedPrefsManager.saveUserSession(response.user, response.token)

                    // Simpan ID User ke Repository (Safe Call)
                    val userId = response.user.id
                    if (!userId.isNullOrEmpty()) {
                        BookRepository.setUserId(userId)
                    }

                    navigateBasedOnRole(response.user.role)
                }
                is LoginState.Error -> {
                    setLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    // Opsional: Reset state agar error tidak muncul lagi saat rotasi layar
                    viewModel.resetState()
                }
                is LoginState.Idle -> {
                    setLoading(false)
                }
            }
        }
    }

    private fun checkAutoLogin() {
        if (sharedPrefsManager.isLoggedIn()) {
            val user = sharedPrefsManager.getUser()
            if (user != null && user.isVerified && !user.id.isNullOrEmpty()) {
                BookRepository.setUserId(user.id)
                navigateBasedOnRole(user.role)
            } else {
                sharedPrefsManager.clearSession()
            }
        }
    }

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