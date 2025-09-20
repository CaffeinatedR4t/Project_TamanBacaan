package com.project.tamanbacaan.models

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val coverUrl: String,
    val readingTime: String,
    val chapters: Int,
    val category: String,
    var isBookmarked: Boolean = false
)