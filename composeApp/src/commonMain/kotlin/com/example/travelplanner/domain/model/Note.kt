package com.example.travelplanner.domain.model

import kotlinx.datetime.Instant

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: NoteCategory,
    val color: NoteColor = NoteColor.DEFAULT,
    val isPinned: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class NoteCategory {
    GENERAL, WORK, PERSONAL, IDEA, TODO
}

enum class NoteColor {
    DEFAULT, RED, BLUE, GREEN, YELLOW, PURPLE
}
