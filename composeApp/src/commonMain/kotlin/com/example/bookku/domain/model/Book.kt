package com.example.bookku.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Book(
    val id: Long = 0,
    val userId: String = "",
    val title: String,
    val content: String,
    val author: String = "",
    val coverUrl: String = "",
    val category: BookGenre = BookGenre.FICTION,
    val color: BookRating = BookRating.DEFAULT,
    val totalPages: Int = 0, // Added total pages
    val isPinned: Boolean = false,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    val preview: String
        get() = if (content.length > 100) content.take(100) + "..." else content

    val isEmpty: Boolean
        get() = title.isBlank() && author.isBlank()
}

enum class BookGenre(val displayName: String) {
    FICTION("Fiksi"),
    NON_FICTION("Non-Fiksi"),
    MYSTERY("Misteri"),
    ROMANCE("Romansa"),
    SCI_FI("Sci-Fi"),
    BIOGRAPHY("Biografi"),
    HISTORY("Sejarah"),
    EDUCATION("Edukasi");
    
    companion object {
        fun fromString(value: String): BookGenre {
            return entries.find { it.name == value } ?: FICTION
        }
    }
}

enum class BookRating(val hexValue: Long) {
    DEFAULT(0xFFFDFDFD),
    RED(0xFFFFEBEE),
    ORANGE(0xFFFFF3E0),
    YELLOW(0xFFFFFDE7),
    GREEN(0xFFE8F5E9),
    BLUE(0xFFE3F2FD),
    PURPLE(0xFFF3E5F5),
    PINK(0xFFFCE4EC);
    
    companion object {
        fun fromString(value: String): BookRating {
            return entries.find { it.name == value } ?: DEFAULT
        }
    }
}
