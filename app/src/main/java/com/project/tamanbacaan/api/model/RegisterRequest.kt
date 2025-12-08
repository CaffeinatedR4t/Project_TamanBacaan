package com.caffeinatedr4t.tamanbacaan.api.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @field:SerializedName("full_name")
    val fullName: String, // Nama lengkap Anggota

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("password")
    val password: String,

    @field:SerializedName("nik")
    val nik: String, // NIK KTP Anggota (atau NIK Orang Tua jika anak)

    @field:SerializedName("is_child")
    val isChild: Boolean = false,

    @field:SerializedName("parent_name")
    val parentName: String? = null,

    @field:SerializedName("address_rt_rw")
    val addressRtRw: String // Informasi RT/RW/Kelurahan untuk verifikasi
)