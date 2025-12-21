package com.caffeinatedr4t.tamanbacaan.models

import com.google.gson.annotations.SerializedName

data class Book(
    @SerializedName("_id")
    val id: String = "",

    val title: String = "",
    val author: String = "",
    val description: String = "",
    @SerializedName("coverImage")
    val coverUrl: String = "",
    val category: String = "",

    // Status buku secara umum (dari collection Books)
    var isAvailable: Boolean = true,
    var stock: Int = 0,

    // [PENTING] Tambahkan ini untuk status Transaksi User
    // Nilainya nanti diisi: "PENDING", "BORROWED", "RETURNED", atau null
    var status: String? = null,

    // Field lain (tetap biarkan seperti adanya)
    var isBookmarked: Boolean = false,
    var isBorrowed: Boolean = false,
    val isbn: String = "",
    @SerializedName("year")
    val publicationYear: Int = 0,
    val borrowedDate: String? = null,
    val dueDate: String? = null,
    val avgRating: Float = 0.0f,
    val totalReviews: Int = 0,
    val synopsis: String = "",
    val publisher: String = "",
    val totalCopies: Int = 0,
    val createdAt: String = ""
) {
    // Helper untuk Adapter
    fun getAvailabilityStatus(): String {
        return when (status) {
            "PENDING" -> "Menunggu Persetujuan"
            "BORROWED" -> "Sedang Dipinjam"
            "RETURNED" -> "Sudah Dikembalikan"
            else -> if (isAvailable && stock > 0) "Tersedia" else "Stok Habis"
        }
    }

    fun getStatusColor(): Int {
        return when (status) {
            "PENDING" -> android.R.color.darker_gray
            "BORROWED" -> android.R.color.holo_orange_dark
            else -> if (isAvailable && stock > 0) android.R.color.holo_green_dark else android.R.color.holo_red_dark
        }
    }
}