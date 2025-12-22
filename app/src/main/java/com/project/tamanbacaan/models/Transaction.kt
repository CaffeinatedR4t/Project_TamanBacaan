package com.caffeinatedr4t.tamanbacaan.models

import com.google.gson.annotations.SerializedName

data class Transaction(
    @SerializedName("_id")
    val id: String? = null,

    val userId: Any,
    val bookId: Any,

    val borrowDate: String? = null,
    val dueDate: String,
    val returnDate: String? = null,
    val status: String = "PENDING",
    val fine: Int = 0,
    val notes: String = ""
)