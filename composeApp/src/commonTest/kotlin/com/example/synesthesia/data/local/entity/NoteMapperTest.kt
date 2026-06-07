package com.example.synesthesia.data.local.entity

import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.model.NoteColor
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Comprehensive unit tests for NoteMapper.
 * Targets 100% line & branch coverage for:
 * - Note.toEntityValues()
 * - NoteEntityValues data class
 */
class NoteMapperTest {

    // ==================== Note.toEntityValues() ====================

    @Test
    fun `toEntityValues should map all fields correctly`() {
        val now = Clock.System.now()
        val note = Note(
            id = 42,
            title = "Test Title",
            content = "Test Content",
            category = NoteCategory.WORK,
            color = NoteColor.JOY,
            emotion = "Enthusiastic",
            artToken = "High Energy, Pleasant",
            aiResonance = "Great vibes!",
            isPinned = true,
            createdAt = now,
            updatedAt = now
        )

        val values = note.toEntityValues()

        assertEquals("Test Title", values.title)
        assertEquals("Test Content", values.content)
        assertEquals("WORK", values.category)
        assertEquals("JOY", values.color)
        assertEquals("Enthusiastic", values.emotion)
        assertEquals("High Energy, Pleasant", values.artToken)
        assertEquals("Great vibes!", values.aiResonance)
        assertEquals(1L, values.isPinned)
        assertEquals(now.toEpochMilliseconds(), values.createdAt)
        assertEquals(now.toEpochMilliseconds(), values.updatedAt)
    }

    @Test
    fun `toEntityValues should map isPinned false to 0L`() {
        val note = Note(
            content = "Test",
            isPinned = false
        )
        val values = note.toEntityValues()
        assertEquals(0L, values.isPinned)
    }

    @Test
    fun `toEntityValues should map isPinned true to 1L`() {
        val note = Note(
            content = "Test",
            isPinned = true
        )
        val values = note.toEntityValues()
        assertEquals(1L, values.isPinned)
    }

    @Test
    fun `toEntityValues should handle null emotion`() {
        val note = Note(content = "Test", emotion = null)
        val values = note.toEntityValues()
        assertNull(values.emotion)
    }

    @Test
    fun `toEntityValues should handle null artToken`() {
        val note = Note(content = "Test", artToken = null)
        val values = note.toEntityValues()
        assertNull(values.artToken)
    }

    @Test
    fun `toEntityValues should handle null aiResonance`() {
        val note = Note(content = "Test", aiResonance = null)
        val values = note.toEntityValues()
        assertNull(values.aiResonance)
    }

    @Test
    fun `toEntityValues should map default category to GENERAL`() {
        val note = Note(content = "Test")
        val values = note.toEntityValues()
        assertEquals("GENERAL", values.category)
    }

    @Test
    fun `toEntityValues should map default color to DEFAULT`() {
        val note = Note(content = "Test")
        val values = note.toEntityValues()
        assertEquals("DEFAULT", values.color)
    }

    @Test
    fun `toEntityValues should map all categories correctly`() {
        NoteCategory.entries.forEach { category ->
            val note = Note(content = "Test", category = category)
            val values = note.toEntityValues()
            assertEquals(category.name, values.category)
        }
    }

    @Test
    fun `toEntityValues should map all colors correctly`() {
        NoteColor.entries.forEach { color ->
            val note = Note(content = "Test", color = color)
            val values = note.toEntityValues()
            assertEquals(color.name, values.color)
        }
    }

    // ==================== NoteEntityValues data class ====================

    @Test
    fun `NoteEntityValues should hold all fields`() {
        val values = NoteEntityValues(
            title = "Title",
            content = "Content",
            category = "WORK",
            color = "JOY",
            emotion = "Happy",
            artToken = "Token",
            aiResonance = "Resonance",
            isPinned = 1L,
            createdAt = 1000L,
            updatedAt = 2000L
        )
        assertEquals("Title", values.title)
        assertEquals("Content", values.content)
        assertEquals("WORK", values.category)
        assertEquals("JOY", values.color)
        assertEquals("Happy", values.emotion)
        assertEquals("Token", values.artToken)
        assertEquals("Resonance", values.aiResonance)
        assertEquals(1L, values.isPinned)
        assertEquals(1000L, values.createdAt)
        assertEquals(2000L, values.updatedAt)
    }

    @Test
    fun `NoteEntityValues with null optional fields`() {
        val values = NoteEntityValues(
            title = "Title",
            content = "Content",
            category = "GENERAL",
            color = "DEFAULT",
            emotion = null,
            artToken = null,
            aiResonance = null,
            isPinned = 0L,
            createdAt = 0L,
            updatedAt = 0L
        )
        assertNull(values.emotion)
        assertNull(values.artToken)
        assertNull(values.aiResonance)
    }
}
