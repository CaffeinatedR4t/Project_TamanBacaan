package com.caffeinatedr4t.tamanbacaan.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("_id", alternate = ["id"])
    val id: String = "",

    val fullName: String = "",

    val email: String = "",

    val nik: String = "",

    val addressRtRw: String = "",

    val addressKelurahan: String = "",
    val addressKecamatan: String = "",
    val phoneNumber: String = "",

    val role: String = "MEMBER",

    val status: String = "Active",

    val isChild:  Boolean = false,

    val parentName: String? = null,

    val isVerified: Boolean = false,

    val password: String = "",

    val bookmarks: List<String> = emptyList()

)