package com.caffeinatedr4t.tamanbacaan.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caffeinatedr4t.tamanbacaan.data.BookRepository
import com.caffeinatedr4t.tamanbacaan.state.TransactionManagementState
import kotlinx.coroutines.launch

class TransactionManagementViewModel : ViewModel() {

    private val _state = MutableLiveData<TransactionManagementState>(TransactionManagementState.Idle)
    val state: LiveData<TransactionManagementState> = _state

    /**
     * Memuat daftar permintaan peminjaman yang pending
     */
    fun loadPendingRequests() {
        _state.value = TransactionManagementState.Loading
        viewModelScope.launch {
            try {
                // Panggil Repository
                val requests = BookRepository.fetchPendingRequests()
                _state.value = TransactionManagementState.SuccessLoad(requests)
            } catch (e: Exception) {
                _state.value = TransactionManagementState.Error("Gagal memuat data: ${e.message}")
            }
        }
    }

    /**
     * Memproses permintaan (Setujui atau Tolak)
     */
    fun processRequest(requestId: String, isApproved: Boolean) {
        _state.value = TransactionManagementState.Loading
        viewModelScope.launch {
            try {
                val success: Boolean
                val actionMessage: String

                if (isApproved) {
                    // Panggil API Approve
                    success = BookRepository.approveRequestApi(requestId)
                    actionMessage = "disetujui"
                } else {
                    // Panggil API Reject
                    success = BookRepository.rejectRequestApi(requestId)
                    actionMessage = "ditolak"
                }

                if (success) {
                    _state.value = TransactionManagementState.SuccessOperation("Permintaan berhasil $actionMessage.")
                    // Otomatis refresh list agar data terbaru muncul
                    loadPendingRequests()
                } else {
                    val failMsg = if (isApproved) "menyetujui" else "menolak"
                    _state.value = TransactionManagementState.Error("Gagal $failMsg permintaan.")
                }
            } catch (e: Exception) {
                _state.value = TransactionManagementState.Error("Error: ${e.message}")
            }
        }
    }

    // Reset state agar pesan tidak muncul berulang
    fun resetState() {
        _state.value = TransactionManagementState.Idle
    }
}