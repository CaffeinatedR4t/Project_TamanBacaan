package com.caffeinatedr4t.tamanbacaan.viewmodels.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caffeinatedr4t.tamanbacaan.data.BookRepository

import com.caffeinatedr4t.tamanbacaan.data.UpdateProfileRequest
import com.caffeinatedr4t.tamanbacaan.utils.SharedPrefsManager
import com.project.tamanbacaan.viewmodels.profile.ProfileState
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: BookRepository,
    private val prefs: SharedPrefsManager
) : ViewModel() {

    private val _profileState = MutableLiveData<ProfileState>()
    val profileState: LiveData<ProfileState> = _profileState

    fun loadProfile() {
        val token = prefs.getUserToken()
        if (token.isNullOrEmpty()) {
            _profileState.value = ProfileState.Error("Token tidak valid")
            return
        }

        viewModelScope.launch {
            _profileState.value = ProfileState.Loading

            val user = repository.getUserProfile(token)
            if (user != null) {
                _profileState.value = ProfileState.Success(user)
            } else {
                _profileState.value = ProfileState.Error("Gagal memuat profil")
            }
        }
    }

    fun updateProfile(request: UpdateProfileRequest) {
        val token = prefs.getUserToken()
        if (token.isNullOrEmpty()) {
            _profileState.value = ProfileState.Error("Token tidak valid")
            return
        }

        viewModelScope.launch {
            _profileState.value = ProfileState.Loading

            val updatedUser = repository.updateMyProfile(token, request)
            if (updatedUser != null) {
                // 🔥 auto refresh UI
                _profileState.value = ProfileState.Success(updatedUser)
            } else {
                _profileState.value = ProfileState.Error("Gagal memperbarui profil")
            }
        }
    }
}

