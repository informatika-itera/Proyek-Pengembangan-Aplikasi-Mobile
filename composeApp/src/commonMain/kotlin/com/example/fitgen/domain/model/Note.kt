package com.example.fitgen.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: NoteCategory = NoteCategory.GENERAL,
    val color: NoteColor = NoteColor.DEFAULT,
    val isPinned: Boolean = false,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    val preview: String
        get() = if (content.length > 100) content.take(100) + "..." else content
    
    val isEmpty: Boolean
        get() = title.isBlank() && content.isBlank()
}

enum class NoteCategory(val displayName: String) {
    GENERAL("Umum"),
    WORK("Pekerjaan"),
    PERSONAL("Pribadi"),
    IDEAS("Ide"),
    TODO("To-Do"),
    STUDY("Belajar");
    
    companion object {
        fun fromString(value: String): NoteCategory {
            return entries.find { it.name == value } ?: GENERAL
        }
    }
}

enum class NoteColor(val hexValue: Long) {
    DEFAULT(0xFFFFFFFF),
    RED(0xFFFFCDD2),
    ORANGE(0xFFFFE0B2),
    YELLOW(0xFFFFF9C4),
    GREEN(0xFFC8E6C9),
    BLUE(0xFFBBDEFB),
    PURPLE(0xFFE1BEE7),
    PINK(0xFFF8BBD9);
    
    companion object {
        fun fromString(value: String): NoteColor {
            return entries.find { it.name == value } ?: DEFAULT
        }
    }
}
