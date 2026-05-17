package com.soundletter.app.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class NoteTest {

    @Test
    fun testNotePreviewShortContent() {
        val content = "Short message"
        val note = Note(
            recipient = "Dzakky",
            content = content
        )
        assertEquals(content, note.preview)
    }

    @Test
    fun testNotePreviewLongContent() {
        val content = "A".repeat(150)
        val expectedPreview = "A".repeat(100) + "..."
        val note = Note(
            recipient = "Atalie",
            content = content
        )
        assertEquals(expectedPreview, note.preview)
    }
}
