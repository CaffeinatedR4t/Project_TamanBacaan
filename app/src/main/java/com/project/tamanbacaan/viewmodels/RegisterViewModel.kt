package com.caffeinatedr4t.tamanbacaan.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caffeinatedr4t.tamanbacaan.api.ApiConfig
import com.caffeinatedr4t.tamanbacaan.api.model.RegisterRequest
import com.caffeinatedr4t.tamanbacaan.state.RegisterState
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _registerState = MutableLiveData<RegisterState>(RegisterState.Idle)
    val registerState: LiveData<RegisterState> = _registerState

    fun register(request: RegisterRequest) {
        _registerState.value = RegisterState.Loading

        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService()
                val response = apiService.register(request)

                if (response.isSuccessful) {
                    val registerResponse = response.body()

                    // Asumsi response body memiliki field 'success' boolean
                    // Sesuaikan dengan response model Anda jika berbeda
                    if (registerResponse != null && registerResponse.success) {
                        _registerState.value = RegisterState.Success("Pendaftaran berhasil! Silakan login dengan akun Anda.")
                    } else {
                        _registerState.value = RegisterState.Error(registerResponse?.message ?: "Pendaftaran gagal")
                    }
                } else {
                    _registerState.value = RegisterState.Error("Gagal mendaftar: ${response.message()}")
                }

            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Error: ${e.message}\nPastikan backend sudah berjalan!")
                e.printStackTrace()
            }
        }
    }

    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}