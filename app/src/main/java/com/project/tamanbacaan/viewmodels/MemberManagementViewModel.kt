package com.caffeinatedr4t.tamanbacaan.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.models.User
import com.caffeinatedr4t.tamanbacaan.state.MemberManagementState
import kotlinx.coroutines.launch

class MemberManagementViewModel : ViewModel() {

    private val _state = MutableLiveData<MemberManagementState>(MemberManagementState.Idle)
    val state: LiveData<MemberManagementState> = _state

    /**
     * Memuat semua daftar anggota
     */
    fun loadMembers() {
        _state.value = MemberManagementState.Loading
        viewModelScope.launch {
            try {
                // Panggil Repository (pastikan repository sudah suspend function atau blocking yang aman)
                val allMembers = BookRepository.getAllMembers()
                _state.value = MemberManagementState.SuccessLoad(allMembers)
            } catch (e: Exception) {
                _state.value = MemberManagementState.Error("Gagal memuat data: ${e.message}")
            }
        }
    }

    /**
     * Mengubah status verifikasi anggota
     */
    fun toggleVerification(user: User) {
        val userId = user.id
        if (userId == null) {
            _state.value = MemberManagementState.Error("ID User tidak valid")
            return
        }

        _state.value = MemberManagementState.Loading
        viewModelScope.launch {
            try {
                val success = BookRepository.toggleVerificationStatus(userId, user.isVerified)
                if (success) {
                    val statusMsg = if (!user.isVerified) "Diverifikasi" else "Dibatalkan verifikasi"
                    _state.value = MemberManagementState.SuccessOperation("Sukses: ${user.fullName} $statusMsg")
                    // Refresh data otomatis
                    loadMembers()
                } else {
                    _state.value = MemberManagementState.Error("Gagal mengubah status verifikasi.")
                }
            } catch (e: Exception) {
                _state.value = MemberManagementState.Error("Error: ${e.message}")
            }
        }
    }

    /**
     * Menghapus anggota
     */
    fun deleteMember(user: User) {
        val userId = user.id
        if (userId == null) {
            _state.value = MemberManagementState.Error("ID User tidak valid")
            return
        }

        _state.value = MemberManagementState.Loading
        viewModelScope.launch {
            try {
                val success = BookRepository.deleteMember(userId)
                if (success) {
                    _state.value = MemberManagementState.SuccessOperation("Anggota berhasil dihapus.")
                    // Refresh data otomatis
                    loadMembers()
                } else {
                    _state.value = MemberManagementState.Error("Gagal menghapus anggota.")
                }
            } catch (e: Exception) {
                _state.value = MemberManagementState.Error("Error: ${e.message}")
            }
        }
    }

    // Reset state agar toast tidak muncul berulang saat rotasi layar
    fun resetState() {
        _state.value = MemberManagementState.Idle
    }
}