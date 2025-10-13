package com.caffeinatedr4t.tamanbacaan.models

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val nik: String,
    val addressRtRw: String,
    val role: String = "member", // "member" atau "admin"
    val status: String = "Active", // "Active" atau "Pending Verification"
    val isChild: Boolean = false,
    val parentName: String? = null
)