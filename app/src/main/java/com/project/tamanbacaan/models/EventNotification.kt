package com.caffeinatedr4t.tamanbacaan.models

import com.google.gson.annotations.SerializedName

data class EventNotification(
    @SerializedName("_id")
    val id: String,

    val title: String,

    val message:  String,

    @SerializedName("createdAt")
    val date: String,

    // ADD THESE NEW FIELDS FROM BACKEND
    val createdBy: String = "",
    val isActive: Boolean = true
)