package com.caffeinatedr4t.tamanbacaan.api.model

data class ReviewRequest(
    val userId: String,
    val userName: String,
    val rating: Double,
    val comment: String
)