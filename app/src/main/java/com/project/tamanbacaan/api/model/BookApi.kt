package com.caffeinatedr4t.tamanbacaan.api.model

import com.google.gson.annotations.SerializedName

// Model ini digunakan untuk menerima data buku dari API
data class BookApi(
    // Asumsi nama field yang mungkin digunakan
    @field:SerializedName("id", alternate = ["book_id", "bookId"])
    val id: String,

    @field:SerializedName("title", alternate = ["judul"])
    val title: String,

    @field:SerializedName("author", alternate = ["penulis", "author_name"])
    val author: String,

    // Untuk API ini, mungkin hanya tersedia data dasar
    @field:SerializedName("description", alternate = ["sinopsis"])
    val description: String? = null,

    @field:SerializedName("category", alternate = ["kategori"])
    val category: String? = null,

    @field:SerializedName("cover", alternate = ["cover_url"])
    val coverUrl: String? = null

    // Kita hapus field 'stock'/'availableCount' karena API publik tidak mungkin menyediakannya.
)