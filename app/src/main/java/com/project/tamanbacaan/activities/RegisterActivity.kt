package com.caffeinatedr4t.tamanbacaan.activities

import android.R.attr.phoneNumber
import android.content. Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget. ProgressBar
import android.widget. TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan. api.ApiConfig
import com. caffeinatedr4t.tamanbacaan.api.model. RegisterRequest
import kotlinx.coroutines.launch

/**
 * Activity untuk registrasi anggota baru dengan koneksi ke Backend API.
 * Mendukung registrasi untuk anggota dewasa dan anak-anak.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etNik: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etAddressRtRw: EditText
    private lateinit var cbIsChild: CheckBox
    private lateinit var etParentName: EditText
    private lateinit var btnRegister:  Button
    private lateinit var tvLogin: TextView
    private lateinit var progressBar:  ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout. activity_register)

        // Initialize UI components
        etFullName = findViewById(R.id.etFullName)
        etNik = findViewById(R.id.etNik)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etAddressRtRw = findViewById(R.id.etAddressRtRw)
        // Add these fields to your XML layout if not exist:
        cbIsChild = findViewById(R.id.cbIsChild)
        etParentName = findViewById(R.id.etParentName)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        progressBar = findViewById(R. id.progressBar) // Add to XML

        // Show/hide parent name field based on checkbox
        cbIsChild.setOnCheckedChangeListener { _, isChecked ->
            etParentName.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) {
                etParentName.text. clear()
            }
        }

        // Register button click listener
        btnRegister.setOnClickListener {
            validateAndRegister()
        }

        // Login text click listener
        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    /**
     * Validate input and call register API
     */
    private fun validateAndRegister() {
        // Get input values
        val fullName = etFullName.text.toString().trim()
        val nik = etNik.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val addressRtRw = etAddressRtRw.text.toString().trim()
        val isChild = cbIsChild.isChecked
        val parentName = if (isChild) etParentName.text.toString().trim() else null

        // Validate required fields
        when {
            fullName.isEmpty() -> {
                Toast.makeText(this, "Nama lengkap tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            nik.length != 16 -> {
                Toast.makeText(this, "NIK harus 16 digit", Toast.LENGTH_SHORT).show()
                return
            }
            email.isEmpty() || !android.util.Patterns. EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast. makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
                return
            }
            password.length < 6 -> {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return
            }
            addressRtRw.isEmpty() -> {
                Toast.makeText(this, "Alamat RT/RW tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            isChild && parentName. isNullOrEmpty() -> {
                Toast.makeText(this, "Nama orang tua wajib diisi untuk anak", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Create register request
        val registerRequest = RegisterRequest(
            fullName = fullName,
            email = email,
            password = password,
            nik = nik,
            addressRtRw = addressRtRw,
            isChild = isChild,
            parentName = parentName
        )

        // Call register API
        registerUser(registerRequest)
    }

    /**
     * Function to call register API
     */
    private fun registerUser(registerRequest: RegisterRequest) {
        // Show loading
        setLoading(true)

        // Call API using coroutine
        lifecycleScope.launch {
            try {
                val apiService = ApiConfig.getApiService()
                val response = apiService.register(registerRequest)

                // Hide loading
                setLoading(false)

                if (response.isSuccessful) {
                    val registerResponse = response.body()

                    if (registerResponse?.success == true) {
                        // Registration successful
                        Toast.makeText(
                            this@RegisterActivity,
                            "Pendaftaran berhasil! Silakan login dengan akun Anda.",
                            Toast. LENGTH_LONG
                        ).show()

                        // Navigate to login
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        // Registration failed
                        Toast. makeText(
                            this@RegisterActivity,
                            registerResponse?.message ?: "Pendaftaran gagal",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // HTTP error
                    Toast.makeText(
                        this@RegisterActivity,
                        "Gagal mendaftar: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e:  Exception) {
                // Network error or other exceptions
                setLoading(false)
                Toast.makeText(
                    this@RegisterActivity,
                    "Error:  ${e.message}\nPastikan backend sudah berjalan! ",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }
    }

    /**
     * Show/hide loading indicator
     */
    private fun setLoading(isLoading:  Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            btnRegister.isEnabled = false
        } else {
            progressBar.visibility = View. GONE
            btnRegister.isEnabled = true
        }
    }
}