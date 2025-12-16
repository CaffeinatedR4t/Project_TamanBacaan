package com.caffeinatedr4t.tamanbacaan.api.model

import com.caffeinatedr4t.tamanbacaan.models.User
import com.google.gson.annotations. SerializedName

data class LoginResponse(
    @SerializedName("token")
    val token: String,

    val success: Boolean,
    val message: String,

    @SerializedName("user")
    val user: User
)
