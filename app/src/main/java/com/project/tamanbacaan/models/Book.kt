package com.caffeinatedr4t.tamanbacaan.models

import com. google.gson.annotations.SerializedName

data class Book(
    @SerializedName("_id")
    val id: String,

    val title: String,

    val author: String,

    val description: String = "",

    @SerializedName("coverImage")
    val coverUrl: String = "",

    val category: String,

    var isBookmarked: Boolean = false,

    var isAvailable: Boolean = true,

    var isBorrowed: Boolean = false,

    val isbn: String = "",

    @SerializedName("year")
    val publicationYear: Int = 0,

    val borrowedDate: String?  = null,

    val dueDate: String? = null,

    // ✅ CHANGED:  Make these fields optional with default values
    val avgRating: Float = 0.0f,
    val totalReviews: Int = 0,
    val synopsis:  String = "", // Will use description as fallback

    // ADD THESE NEW FIELDS FROM BACKEND
    val publisher: String = "",
    val stock: Int = 0,
    val totalCopies: Int = 0,
    val createdAt: String = ""
) {
    fun getAvailabilityStatus(): String {
        return when {
            isBorrowed -> "Borrowed by you"
            ! isAvailable -> "Not Available"
            else -> "Recommended"
        }
    }

    fun getStatusColor(): Int {
        return when {
            isBorrowed -> android.R.color.holo_orange_dark
            !isAvailable -> android.R.color.holo_red_dark
            else -> android. R.color.holo_green_dark
        }
    }
}