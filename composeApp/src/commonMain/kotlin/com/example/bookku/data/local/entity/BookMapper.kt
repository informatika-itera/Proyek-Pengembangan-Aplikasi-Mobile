package com.example.bookku.data.local.entity

import com.example.bookku.data.local.BookEntity
import com.example.bookku.domain.model.Book
import com.example.bookku.domain.model.BookGenre
import com.example.bookku.domain.model.BookRating
import kotlinx.datetime.Instant

fun BookEntity.toDomain(): Book {
    return Book(
        id = id,
        title = title,
        content = content,
        category = BookGenre.fromString(category),
        color = BookRating.fromString(color),
        isPinned = is_pinned == 1L,
        createdAt = Instant.fromEpochMilliseconds(created_at),
        updatedAt = Instant.fromEpochMilliseconds(updated_at)
    )
}

data class BookEntityValues(
    val title: String,
    val content: String,
    val category: String,
    val color: String,
    val isPinned: Long,
    val createdAt: Long,
    val updatedAt: Long
)

fun Book.toEntityValues(): BookEntityValues {
    return BookEntityValues(
        title = title,
        content = content,
        category = category.name,
        color = color.name,
        isPinned = if (isPinned) 1L else 0L,
        createdAt = createdAt.toEpochMilliseconds(),
        updatedAt = updatedAt.toEpochMilliseconds()
    )
}

fun List<BookEntity>.toDomainList(): List<Book> {
    return map { it.toDomain() }
}