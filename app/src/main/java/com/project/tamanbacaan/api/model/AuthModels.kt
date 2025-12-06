package com.caffeinatedr4t.tamanbacaan.api.model

import com.google.gson.annotations.SerializedName

// Registration
data class RegisterRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("nik") val nik: String,
    @SerializedName("addressRtRw") val addressRtRw: String,
    @SerializedName("addressKelurahan") val addressKelurahan: String,
    @SerializedName("addressKecamatan") val addressKecamatan: String,
    @SerializedName("phoneNumber") val phoneNumber: String? = null,
    @SerializedName("isChild") val isChild: Boolean = false,
    @SerializedName("parentName") val parentName: String?  = null
)

data class RegisterResponse(
    @SerializedName("message") val message: String,
    @SerializedName("userId") val userId: String
)

// Login
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserData
)

data class UserData(
    @SerializedName("id") val id: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String,
    @SerializedName("nik") val nik: String,
    @SerializedName("address") val address: String
)