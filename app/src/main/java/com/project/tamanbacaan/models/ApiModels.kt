package com.caffeinatedr4t.tamanbacaan.models

// Login Request
data class LoginRequest(
    val email: String,
    val password: String
)

// Register Request
data class RegisterRequest(
    val fullName: String,
    val nik: String,
    val email: String,
    val password:  String,
    val addressRtRw: String,
    val addressKelurahan:  String,
    val addressKecamatan: String,
    val phoneNumber: String,
    val parentName: String? = null,
    val isChild: Boolean = false
)

// API Response wrapper
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data:  T? = null,
    val token: String? = null,
    val user: User? = null
)

data class RecommendationResponse(
    val source: String,
    val data: List<Book>
)