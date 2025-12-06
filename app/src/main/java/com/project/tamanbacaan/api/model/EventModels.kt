package com.caffeinatedr4t.tamanbacaan.api.model

import com.google.gson.annotations.SerializedName

data class EventResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("createdBy") val createdBy: String?  = null,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("isActive") val isActive: Boolean = true
)

data class CreateEventRequest(
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("createdBy") val createdBy: String? = null
)