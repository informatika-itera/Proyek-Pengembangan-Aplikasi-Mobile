package com.example.bookku.data.local.entity

import com.example.bookku.data.local.BookEntity
import com.example.bookku.domain.model.Book
import com.example.bookku.domain.model.BookGenre
import com.example.bookku.domain.model.BookRating
import kotlinx.datetime.Instant

fun BookEntity.toDomain(): Book {
    return Book(
        id = id,
        userId = user_id,
        title = title,
        content = content,
        author = author,
        coverUrl = cover_url,
        category = BookGenre.fromString(category),
        color = BookRating.fromString(color),
        totalPages = total_pages.toInt(),
        isPinned = is_pinned == 1L,
        createdAt = Instant.fromEpochMilliseconds(created_at),
        updatedAt = Instant.fromEpochMilliseconds(updated_at)
    )
}

data class BookEntityValues(
    val userId: String,
    val title: String,
    val content: String,
    val author: String,
    val coverUrl: String,
    val category: String,
    val color: String,
    val totalPages: Int,
    val isPinned: Long,
    val createdAt: Long,
    val updatedAt: Long
)

fun Book.toEntityValues(): BookEntityValues {
    return BookEntityValues(
        userId = userId,
        title = title,
        content = content,
        author = author,
        coverUrl = coverUrl,
        category = category.name,
        color = color.name,
        totalPages = totalPages,
        isPinned = if (isPinned) 1L else 0L,
        createdAt = createdAt.toEpochMilliseconds(),
        updatedAt = updatedAt.toEpochMilliseconds()
    )
}

fun List<BookEntity>.toDomainList(): List<Book> {
    return map { it.toDomain() }
}