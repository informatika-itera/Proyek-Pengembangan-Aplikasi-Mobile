package com.soundletter.app.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Note(
    val id: Long = 0,
    val recipient: String,
    val sender: String = "Anon",
    val content: String,
    val songTitle: String? = null,
    val songArtist: String? = null,
    val category: NoteCategory = NoteCategory.GENERAL,
    val color: NoteColor = NoteColor.DEFAULT,
    val isPinned: Boolean = false,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    val preview: String
        get() = if (content.length > 100) content.take(100) + "..." else content
}

enum class NoteCategory {
    GENERAL, WORK, PERSONAL, IDEAS, TODO, STUDY;

    companion object {
        fun fromString(value: String): NoteCategory {
            return entries.find { it.name == value } ?: GENERAL
        }
    }
}

enum class NoteColor {
    DEFAULT, RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE, PINK;

    companion object {
        fun fromString(value: String): NoteColor {
            return entries.find { it.name == value } ?: DEFAULT
        }
    }
}
