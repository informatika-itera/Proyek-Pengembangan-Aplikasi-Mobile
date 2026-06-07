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
    fun `test toDomain maps all fields correctly`() {
        val now = 1715950000000L
        val entity = NoteEntity(
            id = 10L,
            recipient = "Gian",
            sender = "Atalie",
            content = "Hello Mapping",
            song_title = "Starboy",
            song_artist = "The Weeknd",
            song_preview_url = "https://audio.com",
            song_album_art_url = "https://image.com",
            category = "WORK",
            color = "PINK",
            is_pinned = 1L,
            created_at = now,
            updated_at = now
        )

        val domain = entity.toDomain()

        assertEquals(entity.id, domain.id)
        assertEquals(entity.recipient, domain.recipient)
        assertEquals(NoteCategory.WORK, domain.category)
        assertEquals(NoteColor.PINK, domain.color)
        assertEquals(true, domain.isPinned)
        assertEquals(Instant.fromEpochMilliseconds(now), domain.createdAt)
        assertEquals("https://audio.com", domain.songPreviewUrl)
    }

    @Test
    fun `test toDomain handles default fallback values`() {
        val entity = NoteEntity(
            id = 1L, recipient = "", sender = "", content = "",
            song_title = null, song_artist = null, song_preview_url = null, song_album_art_url = null,
            category = "INVALID", color = "UNKNOWN", is_pinned = 0L,
            created_at = 0L, updated_at = 0L
        )

        val domain = entity.toDomain()
        assertEquals(NoteCategory.GENERAL, domain.category)
        assertEquals(NoteColor.DEFAULT, domain.color)
        assertEquals(false, domain.isPinned)
    }
}
