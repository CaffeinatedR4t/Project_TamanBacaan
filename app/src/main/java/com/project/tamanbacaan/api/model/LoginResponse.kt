package com.caffeinatedr4t.tamanbacaan.api.model

import com.caffeinatedr4t.tamanbacaan.models.User
import com.google.gson.annotations. SerializedName

data class LoginResponse(
    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field: SerializedName("token")
    val token: String?  = null,

    @field: SerializedName("user")
    val user: User? = null
)