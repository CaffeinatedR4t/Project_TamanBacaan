package com.caffeinatedr4t. tamanbacaan.api.model

import com.google.gson. annotations.SerializedName

data class BookResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("category") val category: String,
    @SerializedName("publisher") val publisher: String?  = null,
    @SerializedName("year") val year: Int? = null,
    @SerializedName("isbn") val isbn: String? = null,
    @SerializedName("stock") val stock: Int = 0,
    @SerializedName("totalCopies") val totalCopies: Int = 0,
    @SerializedName("coverImage") val coverImage: String?  = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("isAvailable") val isAvailable: Boolean = true,
    @SerializedName("createdAt") val createdAt: String? = null
)