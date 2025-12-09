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
        // Show loading
        setLoading(true)

        // Create login request
        val loginRequest = LoginRequest(email, password)

        // Call API using coroutine
        lifecycleScope.launch {
            try {
                val apiService = ApiConfig.getApiService()
                val response = apiService. login(loginRequest)

                // Hide loading
                setLoading(false)

                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    if (loginResponse?.success == true && loginResponse.user != null) {
                        // Login successful
                        Toast.makeText(
                            this@LoginActivity,
                            "Login Berhasil!  Selamat datang ${loginResponse.user.fullName}",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Save user session
                        sharedPrefsManager.saveUserSession(
                            loginResponse.user,
                            loginResponse.token ?: ""
                        )

                        // Navigate based on user role
                        navigateBasedOnRole(loginResponse.user.role)

                    } else {
                        // Login failed - wrong credentials
                        Toast.makeText(
                            this@LoginActivity,
                            loginResponse?.message ?: "Email atau Password salah",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // HTTP error
                    Toast.makeText(
                        this@LoginActivity,
                        "Gagal login: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                // Network error or other exceptions
                setLoading(false)
                Toast.makeText(
                    this@LoginActivity,
                    "Error: ${e.message}\nPastikan backend sudah berjalan! ",
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