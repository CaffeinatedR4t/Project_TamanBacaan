package com.caffeinatedr4t.tamanbacaan.state

import com.caffeinatedr4t.tamanbacaan.data.PendingRequest

sealed class TransactionManagementState {
    // Status diam/awal
    object Idle : TransactionManagementState()

    // Status sedang memuat (loading)
    object Loading : TransactionManagementState()

    // Sukses memuat daftar permintaan
    data class SuccessLoad(val requests: List<PendingRequest>) : TransactionManagementState()

    // Sukses melakukan aksi (Approve/Reject)
    data class SuccessOperation(val message: String) : TransactionManagementState()

    // Terjadi error
    data class Error(val message: String) : TransactionManagementState()
}