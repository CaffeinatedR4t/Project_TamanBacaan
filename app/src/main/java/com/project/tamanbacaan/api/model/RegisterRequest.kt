package com.caffeinatedr4t.tamanbacaan.api.model

import com.google.gson. annotations.SerializedName

data class RegisterRequest(
    @field:SerializedName("fullName")
    val fullName: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("password")
    val password: String,

    @field:SerializedName("nik")
    val nik: String,

    @field:SerializedName("addressRtRw")
    val addressRtRw: String,

    @field:SerializedName("isChild")
    val isChild: Boolean = false,

    @field:SerializedName("parentName")
    val parentName: String? = null
)