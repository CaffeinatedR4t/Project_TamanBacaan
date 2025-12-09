package com.caffeinatedr4t.tamanbacaan.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("_id")
    val id: String = "",

    val fullName: String,

    val email: String,

    val nik: String,

    val addressRtRw: String,

    // ADD THESE NEW FIELDS TO MATCH BACKEND
    val addressKelurahan: String = "",
    val addressKecamatan: String = "",
    val phoneNumber: String = "",

    val role: String = "MEMBER", // Backend uses "MEMBER" or "ADMIN"

    val status: String = "Active",

    val isChild:  Boolean = false,

    val parentName: String? = null,

    val isVerified: Boolean = false,

    // ADD PASSWORD FIELD (only used for registration/login)
    val password: String = ""
)