package com.example.movein.data.local.entity

import com.example.movein.data.local.NoteEntity
import com.example.movein.domain.model.Note
import kotlinx.datetime.Instant

fun NoteEntity.toDomain(): Note {
    return Note(
        id = this.id,
        title = this.title,
        content = this.content,
        category = com.example.movein.domain.model.NoteCategory.fromString(this.category),
        color = com.example.movein.domain.model.NoteColor.fromString(this.color),
        isPinned = this.is_pinned == 1L,
        createdAt = Instant.fromEpochMilliseconds(this.created_at),
        updatedAt = Instant.fromEpochMilliseconds(this.updated_at)
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
        title = this.title,
        content = this.content,
        category = this.category.name,
        color = this.color.name,
        isPinned = if (this.isPinned) 1L else 0L,
        createdAt = this.createdAt.toEpochMilliseconds(),
        updatedAt = this.updatedAt.toEpochMilliseconds()
    )
}

fun List<NoteEntity>.toDomainList(): List<Note> {
    return this.map { it.toDomain() }
}