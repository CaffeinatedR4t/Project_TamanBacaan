package com.caffeinatedr4t.tamanbacaan.utils

import com.caffeinatedr4t.tamanbacaan.api.model.BookResponse
import com.caffeinatedr4t.tamanbacaan.models.Book

/**
 * Extension function to convert BookResponse (API model) to Book (UI model)
 */
fun BookResponse.toBook(): Book {
    return Book(
        id = this.id,
        title = this.title,
        author = this.author,
        description = this.description ?: "No description available",
        coverUrl = this.coverImage ?: "",
        category = this.category,
        isAvailable = this.isAvailable,
        isbn = this.isbn ?: "",
        publicationYear = this.year ?: 0
    )
}
