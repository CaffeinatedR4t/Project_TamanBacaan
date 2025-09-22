package com.project.tamanbacaan.models

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val coverUrl: String,
    val category: String,
    var isBookmarked: Boolean = false,
    var isAvailable: Boolean = true,
    var isBorrowed: Boolean = false,
    val isbn: String = "",
    val publicationYear: Int = 0,
    val borrowedDate: String? = null,
    val dueDate: String? = null
) {

    fun getAvailabilityStatus(): String {
        return when {
            isBorrowed -> "Borrowed by you"
            !isAvailable -> "Not Available"
            else -> "Available"
        }
    }

    fun getStatusColor(): Int {
        return when {
            isBorrowed -> android.R.color.holo_orange_dark
            !isAvailable -> android.R.color.holo_red_dark
            else -> android.R.color.holo_green_dark
        }
    }
}