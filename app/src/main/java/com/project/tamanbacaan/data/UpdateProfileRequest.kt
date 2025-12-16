package com.caffeinatedr4t.tamanbacaan.data

data class UpdateProfileRequest(
    val fullName: String,
    val email: String,
    val addressRtRw: String? = null,
    val addressKelurahan: String? = null,
    val addressKecamatan: String? = null
)
