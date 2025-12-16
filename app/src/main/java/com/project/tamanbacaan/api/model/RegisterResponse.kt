package com.project.tamanbacaan.api.model

import com.caffeinatedr4t.tamanbacaan.models.User
import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("user")
    val user: User,

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String
)
