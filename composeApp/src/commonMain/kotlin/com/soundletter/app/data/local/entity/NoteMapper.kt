package com.soundletter.app.data.local.entity

import com.soundletter.app.data.local.NoteEntity
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.model.NoteCategory
import com.soundletter.app.domain.model.NoteColor
import kotlinx.datetime.Instant

/**
 * Mapper untuk mengubah entitas database (NoteEntity) menjadi model domain (Note)
 */
fun NoteEntity.toDomain(): Note {
    return Note(
        id = this.id,
        recipient = this.recipient,
        sender = this.sender,
        content = this.content,
        songTitle = this.song_title,
        songArtist = this.song_artist,
        category = NoteCategory.fromString(this.category),
        color = NoteColor.fromString(this.color),
        isPinned = this.is_pinned == 1L,
        createdAt = Instant.fromEpochMilliseconds(this.created_at),
        updatedAt = Instant.fromEpochMilliseconds(this.updated_at)
    )
}

/**
 * Mapper untuk mengubah model domain (Note) menjadi entitas database (NoteEntity)
 */
fun Note.toEntityValues(): NoteEntity {
    return NoteEntity(
        id = this.id,
        recipient = this.recipient,
        sender = this.sender,
        content = this.content,
        song_title = this.songTitle,
        song_artist = this.songArtist,
        category = this.category.name,
        color = this.color.name,
        is_pinned = if (this.isPinned) 1L else 0L,
        created_at = this.createdAt.toEpochMilliseconds(),
        updated_at = this.updatedAt.toEpochMilliseconds()
    )
}

fun List<NoteEntity>.toDomainList(): List<Note> {
    return map { it.toDomain() }
}
