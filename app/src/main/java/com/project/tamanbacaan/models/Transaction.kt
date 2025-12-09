package com.caffeinatedr4t.tamanbacaan.models

import com.google.gson. annotations.SerializedName

data class Transaction(
    @SerializedName("_id")
    val id: String = "",
    val userId: String,
    val bookId: String,
    val borrowDate: String,
    val dueDate: String,
    val returnDate: String?  = null,
    val status: String = "PENDING", // PENDING, APPROVED, BORROWED, RETURNED, OVERDUE
    val fine: Int = 0,
    val notes: String = ""
)