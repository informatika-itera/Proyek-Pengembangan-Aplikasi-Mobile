package com.example.Feelia.data.local.entity

import com.example.Feelia.data.local.NoteEntity
import com.example.Feelia.domain.model.Emotion
import com.example.Feelia.domain.model.Note
import kotlinx.datetime.Instant

// udh di fixkan
fun NoteEntity.toDomain(): Note {
    return Note(
        id = id,
        content = content,
        emotion = Emotion.fromString(emotion),
        isPinned = is_pinned == 1L,
        createdAt = Instant.fromEpochMilliseconds(created_at),
        updatedAt = Instant.fromEpochMilliseconds(updated_at)
    )
}

data class NoteEntityValues(
    val content: String,
    val emotion: String,
    val isPinned: Long,
    val createdAt: Long,
    val updatedAt: Long
)

fun Note.toEntityValues(): NoteEntityValues {
    return NoteEntityValues(
        content = content,
        emotion = emotion.name,
        isPinned = if (isPinned) 1L else 0L,
        createdAt = createdAt.toEpochMilliseconds(),
        updatedAt = updatedAt.toEpochMilliseconds()
    )
}

fun List<NoteEntity>.toDomainList(): List<Note> {
    return map { it.toDomain() }
}