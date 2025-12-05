package com.caffeinatedr4t.tamanbacaan.activities.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.api.ApiConfig
import com.caffeinatedr4t.tamanbacaan.api.model.RegisterRequest
import com.caffeinatedr4t.tamanbacaan.api.model.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Activity untuk pendaftaran anggota baru dengan integrasi backend API.
 * Mendukung pendaftaran anggota dewasa dan anak-anak.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etNik: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etAddressRtRw: EditText
    private lateinit var cbIsChild: CheckBox
    private lateinit var etParentName: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        etFullName = findViewById(R.id.etFullName)
        etNik = findViewById(R.id.etNik)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etAddressRtRw = findViewById(R.id.etAddressRtRw)
        cbIsChild = findViewById(R.id.cbIsChild)
        etParentName = findViewById(R.id.etParentName)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        // Show/hide parent name field based on checkbox
        cbIsChild.setOnCheckedChangeListener { _, isChecked ->
            etParentName.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) {
                etParentName.text.clear()
            }
        }

        // Register button click
        btnRegister.setOnClickListener {
            performRegistration()
        }

        // Login link click
        tvLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun performRegistration() {
        val fullName = etFullName.text.toString().trim()
        val nik = etNik.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val address = etAddressRtRw.text.toString().trim()
        val isChild = cbIsChild.isChecked
        val parentName = if (isChild) etParentName.text.toString().trim() else null

        // Validate input
        if (!validateInput(fullName, nik, email, password, address, isChild, parentName)) {
            return
        }

        // Create register request
        // Note: The registration form currently has a single address field for RT/RW/Kelurahan.
        // For proper administrative division tracking, consider adding separate fields for
        // Kelurahan and Kecamatan in activity_register.xml in future updates.
        val registerRequest = RegisterRequest(
            fullName = fullName,
            email = email,
            password = password,
            nik = nik,
            addressRtRw = address,
            addressKelurahan = "To be verified", // Will be updated during admin verification
            addressKecamatan = "To be verified", // Will be updated during admin verification
            phoneNumber = null, // Optional field not included in current form
            isChild = isChild,
            parentName = parentName
        )

        showLoading(true)

        val apiService = ApiConfig.getApiService()
        apiService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                showLoading(false)

                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null) {
                        Toast.makeText(
                            this@RegisterActivity,
                            registerResponse.message,
                            Toast.LENGTH_LONG
                        ).show()

                        // Navigate to login with registered email
                        navigateToLogin(email)
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        400 -> "Data tidak valid. Periksa kembali input Anda."
                        409 -> "Email atau NIK sudah terdaftar."
                        else -> "Pendaftaran gagal. Silakan coba lagi."
                    }
                    Toast.makeText(this@RegisterActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(
                    this@RegisterActivity,
                    "Kesalahan koneksi: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun validateInput(
        fullName: String,
        nik: String,
        email: String,
        password: String,
        address: String,
        isChild: Boolean,
        parentName: String?
    ): Boolean {
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Nama lengkap harus diisi", Toast.LENGTH_SHORT).show()
            etFullName.requestFocus()
            return false
        }

        if (nik.length != 16) {
            Toast.makeText(this, "NIK harus 16 digit", Toast.LENGTH_SHORT).show()
            etNik.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Email harus diisi", Toast.LENGTH_SHORT).show()
            etEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show()
            etEmail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password harus diisi", Toast.LENGTH_SHORT).show()
            etPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
            etPassword.requestFocus()
            return false
        }

        if (address.isEmpty()) {
            Toast.makeText(this, "Alamat harus diisi", Toast.LENGTH_SHORT).show()
            etAddressRtRw.requestFocus()
            return false
        }

        if (isChild && parentName.isNullOrEmpty()) {
            Toast.makeText(this, "Nama orang tua wajib diisi untuk anak", Toast.LENGTH_SHORT).show()
            etParentName.requestFocus()
            return false
        }

        return true
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnRegister.isEnabled = !isLoading
        etFullName.isEnabled = !isLoading
        etNik.isEnabled = !isLoading
        etEmail.isEnabled = !isLoading
        etPassword.isEnabled = !isLoading
        etAddressRtRw.isEnabled = !isLoading
        cbIsChild.isEnabled = !isLoading
        etParentName.isEnabled = !isLoading
        tvLogin.isEnabled = !isLoading
    }

    private fun navigateToLogin(email: String? = null) {
        val intent = Intent(this, LoginActivity::class.java)
        email?.let {
            intent.putExtra("REGISTERED_EMAIL", it)
        }
        startActivity(intent)
        finish()
    }
}