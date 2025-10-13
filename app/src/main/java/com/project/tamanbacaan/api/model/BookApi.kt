package com.caffeinatedr4t.tamanbacaan.api.model

// Model ini digunakan untuk mengirim/menerima data buku dari API
data class BookApi(
    val id: String,
    val title: String,
    val author: String,
    val synopsis: String,
    val coverUrl: String,
    val category: String,
    val stock: Int
)