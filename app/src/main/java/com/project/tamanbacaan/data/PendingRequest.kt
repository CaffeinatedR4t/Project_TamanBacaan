package com.caffeinatedr4t.tamanbacaan.data

import com.caffeinatedr4t.tamanbacaan.models.Book

data class PendingRequest(
    val requestId: String,
    val book: Book,
    val memberName: String,
    val memberId: String,
    val requestDate: String
)