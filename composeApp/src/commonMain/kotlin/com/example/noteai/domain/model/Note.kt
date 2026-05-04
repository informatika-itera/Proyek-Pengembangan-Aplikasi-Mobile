package com.example.noteai.domain.model

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
    DEFAULT(0xFF121212), // Dark Gray for OLED
    RED(0xFF442C2C),
    ORANGE(0xFF44362C),
    YELLOW(0xFF44442C),
    GREEN(0xFF2C442C),
    BLUE(0xFF2C3644),
    PURPLE(0xFF362C44),
    PINK(0xFF442C36);
    
    companion object {
        fun fromString(value: String): NoteColor {
            return entries.find { it.name == value } ?: DEFAULT
        }
    }
}
