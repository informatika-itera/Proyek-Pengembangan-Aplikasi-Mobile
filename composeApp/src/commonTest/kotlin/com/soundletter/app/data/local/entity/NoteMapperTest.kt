package com.soundletter.app.data.local.entity

import com.soundletter.app.data.local.NoteEntity
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.model.NoteCategory
import com.soundletter.app.domain.model.NoteColor
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class NoteMapperTest {

    @Test
    fun testToDomainMapping() {
        val now = 1715950000000L
        val entity = NoteEntity(
            id = 1L,
            recipient = "Dzakky",
            sender = "Anon",
            content = "Unit testing content",
            song_title = "Starboy",
            song_artist = "The Weeknd",
            category = "WORK",
            color = "BLUE",
            is_pinned = 1L,
            created_at = now,
            updated_at = now
        )

        val domain = entity.toDomain()

        assertEquals(entity.id, domain.id)
        assertEquals(entity.recipient, domain.recipient)
        assertEquals(entity.sender, domain.sender)
        assertEquals(NoteCategory.WORK, domain.category)
        assertEquals(NoteColor.BLUE, domain.color)
        assertEquals(true, domain.isPinned)
        assertEquals(Instant.fromEpochMilliseconds(now), domain.createdAt)
    }

    @Test
    fun testToEntityValuesMapping() {
        val now = Instant.fromEpochMilliseconds(1715950000000L)
        val note = Note(
            id = 2L,
            recipient = "Atalie",
            sender = "Dzakky",
            content = "Testing domain to entity",
            songTitle = "After Hours",
            songArtist = "The Weeknd",
            category = NoteCategory.PERSONAL,
            color = NoteColor.PINK,
            isPinned = false,
            createdAt = now,
            updatedAt = now
        )

        val entity = note.toEntityValues()

        assertEquals(note.recipient, entity.recipient)
        assertEquals(note.sender, entity.sender)
        assertEquals("PERSONAL", entity.category)
        assertEquals("PINK", entity.color)
        assertEquals(0L, entity.is_pinned)
        assertEquals(now.toEpochMilliseconds(), entity.created_at)
    }
}
