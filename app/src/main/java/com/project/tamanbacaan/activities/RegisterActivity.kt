// RegisterActivity.kt
package com.caffeinatedr4t.tamanbacaan.activities

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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.caffeinatedr4t.tamanbacaan.R
import com.caffeinatedr4t.tamanbacaan.api.model.RegisterRequest
import com.caffeinatedr4t.tamanbacaan.state.RegisterState
import com.caffeinatedr4t.tamanbacaan.viewmodels.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etNik: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etAddressRtRw: EditText
    private lateinit var etAddressKelurahan: EditText
    private lateinit var etAddressKecamatan: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var cbIsChild: CheckBox
    private lateinit var etParentName: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var progressBar: ProgressBar

    // Inisialisasi ViewModel
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        setupListeners()
        observeViewModel()
    }

    private fun initViews() {
        etFullName = findViewById(R.id.etFullName)
        etNik = findViewById(R.id.etNik)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etAddressRtRw = findViewById(R.id.etAddressRtRw)
        etAddressKelurahan = findViewById(R.id.etAddressKelurahan)
        etAddressKecamatan = findViewById(R.id.etAddressKecamatan)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
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

        btnRegister.setOnClickListener {
            validateAndSubmit()
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    /**
     * Mengamati perubahan state dari ViewModel
     */
    private fun observeViewModel() {
        viewModel.registerState.observe(this) { state ->
            when (state) {
                is RegisterState.Loading -> {
                    setLoading(true)
                }
                is RegisterState.Success -> {
                    setLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()

                    // Arahkan ke Login setelah sukses
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is RegisterState.Error -> {
                    setLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetState() // Reset agar error tidak muncul berulang saat rotasi layar
                }
                is RegisterState.Idle -> {
                    setLoading(false)
                }
            }
        }
    }

    /**
     * Validasi input UI dan kirim data ke ViewModel
     */
    private fun validateAndSubmit() {
        // Get input values
        val fullName = etFullName.text.toString().trim()
        val nik = etNik.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val addressRtRw = etAddressRtRw.text.toString().trim()
        val addressKelurahan = etAddressKelurahan.text.toString().trim()
        val addressKecamatan = etAddressKecamatan.text.toString().trim()
        val phoneNumber = etPhoneNumber.text.toString().trim()
        val isChild = cbIsChild.isChecked
        val parentName = if (isChild) etParentName.text.toString().trim() else null

        // Validate required fields (Validation Logic tetap di Activity untuk feedback instan UI)
        when {
            fullName.isEmpty() -> {
                etFullName.error = "Nama lengkap wajib diisi"
                return
            }
            nik.length != 16 -> {
                etNik.error = "NIK harus 16 digit"
                return
            }
            email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                etEmail.error = "Email tidak valid"
                return
            }
            password.length < 6 -> {
                etPassword.error = "Password minimal 6 karakter"
                return
            }
            addressRtRw.isEmpty() -> {
                etAddressRtRw.error = "Alamat RT/RW wajib diisi"
                return
            }
            addressKelurahan.isEmpty() -> {
                etAddressKelurahan.error = "Kelurahan wajib diisi"
                return
            }
            addressKecamatan.isEmpty() -> {
                etAddressKecamatan.error = "Kecamatan wajib diisi"
                return
            }
            phoneNumber.isEmpty() -> {
                etPhoneNumber.error = "Nomor telepon wajib diisi"
                return
            }
            isChild && parentName.isNullOrEmpty() -> {
                etParentName.error = "Nama orang tua wajib diisi untuk anak"
                return
            }
        }

        // Create register request object
        val registerRequest = RegisterRequest(
            fullName = fullName,
            email = email,
            password = password,
            nik = nik,
            addressRtRw = addressRtRw,
            addressKelurahan = addressKelurahan,
            addressKecamatan = addressKecamatan,
            phoneNumber = phoneNumber,
            isChild = isChild,
            parentName = parentName
        )

        // Delegate API call to ViewModel
        viewModel.register(registerRequest)
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            btnRegister.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            btnRegister.isEnabled = true
        }
    }
}