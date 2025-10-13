package com.caffeinatedr4t.tamanbacaan.models

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val nik: String,
    val addressRtRw: String,
    val role: String = "member",
    val status: String = "Active",
    val isChild: Boolean = false,
    val parentName: String? = null,
    val isVerified: Boolean = false // BARU: Status Verifikasi RT/RW
)