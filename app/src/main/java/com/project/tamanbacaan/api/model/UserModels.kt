package com.caffeinatedr4t.tamanbacaan. api.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("nik") val nik: String,
    @SerializedName("email") val email: String,
    @SerializedName("addressRtRw") val addressRtRw: String,
    @SerializedName("addressKelurahan") val addressKelurahan: String,
    @SerializedName("addressKecamatan") val addressKecamatan: String,
    @SerializedName("phoneNumber") val phoneNumber: String?  = null,
    @SerializedName("parentName") val parentName: String? = null,
    @SerializedName("isChild") val isChild: Boolean = false,
    @SerializedName("role") val role: String = "MEMBER",
    @SerializedName("isVerified") val isVerified: Boolean = false,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class CreateUserRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("nik") val nik: String,
    @SerializedName("addressRtRw") val addressRtRw: String,
    @SerializedName("addressKelurahan") val addressKelurahan: String,
    @SerializedName("addressKecamatan") val addressKecamatan: String,
    @SerializedName("phoneNumber") val phoneNumber: String? = null,
    @SerializedName("parentName") val parentName: String? = null,
    @SerializedName("isChild") val isChild: Boolean = false,
    @SerializedName("isVerified") val isVerified: Boolean = false
)