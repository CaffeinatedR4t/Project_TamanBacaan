package com.caffeinatedr4t.tamanbacaan.api.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("role")
    val role: String? = null, // "member" atau "admin"

    @field:SerializedName("token")
    val token: String? = null
)