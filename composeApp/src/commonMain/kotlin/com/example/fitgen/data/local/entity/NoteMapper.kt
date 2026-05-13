package com.example.fitgen.data.local.entity

import com.example.fitgen.data.local.NoteEntity
import com.example.fitgen.domain.model.Note
import com.example.fitgen.domain.model.NoteCategory
import com.example.fitgen.domain.model.NoteColor
import kotlinx.datetime.Instant

fun NoteEntity.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        category = NoteCategory.fromString(category),
        color = NoteColor.fromString(color),
        isPinned = is_pinned == 1L,
        createdAt = Instant.fromEpochMilliseconds(created_at),
        updatedAt = Instant.fromEpochMilliseconds(updated_at)
    )
}

data class NoteEntityValues(
    val title: String,
    val content: String,
    val category: String,
    val color: String,
    val isPinned: Long,
    val createdAt: Long,
    val updatedAt: Long
)

fun Note.toEntityValues(): NoteEntityValues {
    return NoteEntityValues(
        title = title,
        content = content,
        category = category.name,
        color = color.name,
        isPinned = if (isPinned) 1L else 0L,
        createdAt = createdAt.toEpochMilliseconds(),
        updatedAt = updatedAt.toEpochMilliseconds()
    )
}

fun List<NoteEntity>.toDomainList(): List<Note> {
    return map { it.toDomain() }
}