package com.caffeinatedr4t.tamanbacaan.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caffeinatedr4t.tamanbacaan.api.ApiConfig
import com.caffeinatedr4t.tamanbacaan.api.model.LoginRequest
import com.caffeinatedr4t.tamanbacaan.state.LoginState
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // MutableLiveData untuk diubah di dalam ViewModel
    private val _loginState = MutableLiveData<LoginState>()
    // LiveData (immutable) untuk diobservasi oleh Activity
    val loginState: LiveData<LoginState> = _loginState

    fun login(email: String, password: String) {
        // 1. Validasi Input Awal
        if (email.isEmpty() || password.isEmpty()) {
            _loginState.value = LoginState.Error("Email dan Password tidak boleh kosong")
            return
        }

        // 2. Set status ke Loading
        _loginState.value = LoginState.Loading

        // 3. Panggil API di Background
        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService()
                val loginRequest = LoginRequest(email, password)
                val response = apiService.login(loginRequest)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        // 🔒 Cek Verifikasi User (Logika Bisnis dipindah ke sini)
                        if (!loginResponse.user.isVerified) {
                            _loginState.value = LoginState.Error("Akun Anda belum diverifikasi oleh admin.\nSilakan tunggu proses verifikasi.")
                        } else {
                            // ✅ Login Sah
                            _loginState.value = LoginState.Success(loginResponse)
                        }
                    } else {
                        _loginState.value = LoginState.Error("Login gagal: response kosong")
                    }
                } else {
                    // ❌ HTTP Error
                    _loginState.value = LoginState.Error("Gagal login: ${response.message()}")
                }
            } catch (e: Exception) {
                // Error Jaringan/Exception
                _loginState.value = LoginState.Error("Error: ${e.message}\nPastikan backend sudah berjalan!")
                e.printStackTrace()
            }
        }
    }

    // Fungsi untuk mereset state jika diperlukan (misal setelah error ditampilkan)
    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}