package com.caffeinatedr4t.tamanbacaan.state

import com.caffeinatedr4t.tamanbacaan.models.User

sealed class MemberManagementState {
    // Status diam/awal
    object Idle : MemberManagementState()

    // Status sedang memuat (loading)
    object Loading : MemberManagementState()

    // Sukses memuat data anggota
    data class SuccessLoad(val members: List<User>) : MemberManagementState()

    // Sukses melakukan operasi (Verifikasi/Hapus)
    data class SuccessOperation(val message: String) : MemberManagementState()

    // Terjadi error
    data class Error(val message: String) : MemberManagementState()
}