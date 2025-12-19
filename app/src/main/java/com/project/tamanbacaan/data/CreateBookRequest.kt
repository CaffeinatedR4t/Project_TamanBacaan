package com.caffeinatedr4t.tamanbacaan.data

data class CreateBookRequest(
    val title: String,
    val author: String,
    val category: String,
    val publisher: String? = null,
    val year: Int? = null,
    val isbn: String? = null,
    val stock: Int,
    val totalCopies: Int,
    val description: String,
    val coverImage: String? = null
)
