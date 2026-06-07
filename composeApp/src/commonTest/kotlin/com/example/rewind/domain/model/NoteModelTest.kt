package com.example.rewind.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NoteModelTest {

    // ==================== preview ====================

    @Test
    fun `preview should truncate content longer than 100 characters`() {
        val longContent = "X".repeat(150)
        val note = Note(title = "Title", content = longContent)
        assertEquals("X".repeat(100) + "...", note.preview)
        assertEquals(103, note.preview.length)
    }

    @Test
    fun `preview should not truncate content of exactly 100 characters`() {
        val content = "Y".repeat(100)
        val note = Note(title = "Title", content = content)
        assertEquals(content, note.preview)
    }

    @Test
    fun `preview should not truncate short content`() {
        val note = Note(title = "Title", content = "Short content")
        assertEquals("Short content", note.preview)
    }

    // ==================== isEmpty ====================

    @Test
    fun `isEmpty should return true when both title and content are blank`() {
        val note = Note(title = "", content = "   ")
        assertTrue(note.isEmpty)
    }

    @Test
    fun `isEmpty should return false when title is not blank`() {
        val note = Note(title = "Has Title", content = "")
        assertFalse(note.isEmpty)
    }

    @Test
    fun `isEmpty should return false when content is not blank`() {
        val note = Note(title = "", content = "Has Content")
        assertFalse(note.isEmpty)
    }

    @Test
    fun `isEmpty should return false when both title and content are not blank`() {
        val note = Note(title = "Title", content = "Content")
        assertFalse(note.isEmpty)
    }

    // ==================== NoteCategory.fromString ====================

    @Test
    fun `NoteCategory fromString should return WORK for valid string`() {
        assertEquals(NoteCategory.WORK, NoteCategory.fromString("WORK"))
    }

    @Test
    fun `NoteCategory fromString should return IDEAS for valid string`() {
        assertEquals(NoteCategory.IDEAS, NoteCategory.fromString("IDEAS"))
    }

    @Test
    fun `NoteCategory fromString should return GENERAL for unknown string`() {
        assertEquals(NoteCategory.GENERAL, NoteCategory.fromString("INVALID"))
    }

    // ==================== NoteColor.fromString ====================

    @Test
    fun `NoteColor fromString should return RED for valid string`() {
        assertEquals(NoteColor.RED, NoteColor.fromString("RED"))
    }

    @Test
    fun `NoteColor fromString should return BLUE for valid string`() {
        assertEquals(NoteColor.BLUE, NoteColor.fromString("BLUE"))
    }

    @Test
    fun `NoteColor fromString should return DEFAULT for unknown string`() {
        assertEquals(NoteColor.DEFAULT, NoteColor.fromString("UNKNOWN"))
    }

    // ==================== NoteColor hexValues ====================

    @Test
    fun `NoteColor RED should have correct hex value`() {
        assertEquals(0xFFFFCDD2, NoteColor.RED.hexValue)
    }

    @Test
    fun `NoteColor BLUE should have correct hex value`() {
        assertEquals(0xFFBBDEFB, NoteColor.BLUE.hexValue)
    }
}
