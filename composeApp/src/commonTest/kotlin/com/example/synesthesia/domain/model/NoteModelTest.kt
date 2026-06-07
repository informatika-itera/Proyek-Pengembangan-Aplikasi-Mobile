package com.example.synesthesia.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Comprehensive unit tests for domain model classes.
 * Targets 100% line & branch coverage for:
 * - Note (preview, isEmpty)
 * - NoteCategory (fromString with valid/invalid values)
 * - NoteColor (fromString with valid/invalid values)
 * - EmotionSystem (getCategoryBySubEmotion)
 * - EmotionCategory data class
 */
class NoteModelTest {

    // ==================== Note Tests ====================

    @Test
    fun `Note preview should truncate content longer than 100 chars`() {
        val longContent = "A".repeat(150)
        val note = Note(content = longContent)
        assertEquals(103, note.preview.length)  // 100 + "..."
        assertTrue(note.preview.endsWith("..."))
    }

    @Test
    fun `Note preview should return full content when 100 chars or less`() {
        val shortContent = "A".repeat(100)
        val note = Note(content = shortContent)
        assertEquals(shortContent, note.preview)
        assertFalse(note.preview.endsWith("..."))
    }

    @Test
    fun `Note preview should return full content when shorter than 100 chars`() {
        val content = "Hello World"
        val note = Note(content = content)
        assertEquals(content, note.preview)
    }

    @Test
    fun `Note isEmpty should be true when both title and content are blank`() {
        val note = Note(title = "", content = "")
        assertTrue(note.isEmpty)
    }

    @Test
    fun `Note isEmpty should be true when both title and content are whitespace`() {
        val note = Note(title = "   ", content = "  ")
        assertTrue(note.isEmpty)
    }

    @Test
    fun `Note isEmpty should be false when title is not blank`() {
        val note = Note(title = "Has Title", content = "")
        assertFalse(note.isEmpty)
    }

    @Test
    fun `Note isEmpty should be false when content is not blank`() {
        val note = Note(title = "", content = "Has Content")
        assertFalse(note.isEmpty)
    }

    @Test
    fun `Note isEmpty should be false when both are not blank`() {
        val note = Note(title = "Title", content = "Content")
        assertFalse(note.isEmpty)
    }

    @Test
    fun `Note default values should be correct`() {
        val note = Note(content = "Test")
        assertEquals(0, note.id)
        assertEquals("", note.title)
        assertEquals(NoteCategory.GENERAL, note.category)
        assertEquals(NoteColor.DEFAULT, note.color)
        assertNull(note.emotion)
        assertNull(note.artToken)
        assertNull(note.aiResonance)
        assertFalse(note.isPinned)
    }

    // ==================== NoteCategory Tests ====================

    @Test
    fun `NoteCategory fromString should return GENERAL for matching value`() {
        assertEquals(NoteCategory.GENERAL, NoteCategory.fromString("GENERAL"))
    }

    @Test
    fun `NoteCategory fromString should return WORK for matching value`() {
        assertEquals(NoteCategory.WORK, NoteCategory.fromString("WORK"))
    }

    @Test
    fun `NoteCategory fromString should return PERSONAL for matching value`() {
        assertEquals(NoteCategory.PERSONAL, NoteCategory.fromString("PERSONAL"))
    }

    @Test
    fun `NoteCategory fromString should return IDEAS for matching value`() {
        assertEquals(NoteCategory.IDEAS, NoteCategory.fromString("IDEAS"))
    }

    @Test
    fun `NoteCategory fromString should return TODO for matching value`() {
        assertEquals(NoteCategory.TODO, NoteCategory.fromString("TODO"))
    }

    @Test
    fun `NoteCategory fromString should return STUDY for matching value`() {
        assertEquals(NoteCategory.STUDY, NoteCategory.fromString("STUDY"))
    }

    @Test
    fun `NoteCategory fromString should return GENERAL for invalid value`() {
        assertEquals(NoteCategory.GENERAL, NoteCategory.fromString("INVALID"))
    }

    @Test
    fun `NoteCategory fromString should return GENERAL for empty string`() {
        assertEquals(NoteCategory.GENERAL, NoteCategory.fromString(""))
    }

    @Test
    fun `NoteCategory displayName values should be correct`() {
        assertEquals("Umum", NoteCategory.GENERAL.displayName)
        assertEquals("Pekerjaan", NoteCategory.WORK.displayName)
        assertEquals("Pribadi", NoteCategory.PERSONAL.displayName)
        assertEquals("Ide", NoteCategory.IDEAS.displayName)
        assertEquals("To-Do", NoteCategory.TODO.displayName)
        assertEquals("Belajar", NoteCategory.STUDY.displayName)
    }

    @Test
    fun `NoteCategory entries should have 6 values`() {
        assertEquals(6, NoteCategory.entries.size)
    }

    // ==================== NoteColor Tests ====================

    @Test
    fun `NoteColor fromString should return DEFAULT for matching value`() {
        assertEquals(NoteColor.DEFAULT, NoteColor.fromString("DEFAULT"))
    }

    @Test
    fun `NoteColor fromString should return JOY for matching value`() {
        assertEquals(NoteColor.JOY, NoteColor.fromString("JOY"))
    }

    @Test
    fun `NoteColor fromString should return MELANCHOLY for matching value`() {
        assertEquals(NoteColor.MELANCHOLY, NoteColor.fromString("MELANCHOLY"))
    }

    @Test
    fun `NoteColor fromString should return CALM for matching value`() {
        assertEquals(NoteColor.CALM, NoteColor.fromString("CALM"))
    }

    @Test
    fun `NoteColor fromString should return ANGER for matching value`() {
        assertEquals(NoteColor.ANGER, NoteColor.fromString("ANGER"))
    }

    @Test
    fun `NoteColor fromString should return REFLECTIVE for matching value`() {
        assertEquals(NoteColor.REFLECTIVE, NoteColor.fromString("REFLECTIVE"))
    }

    @Test
    fun `NoteColor fromString should return DEFAULT for invalid value`() {
        assertEquals(NoteColor.DEFAULT, NoteColor.fromString("INVALID"))
    }

    @Test
    fun `NoteColor fromString should return DEFAULT for empty string`() {
        assertEquals(NoteColor.DEFAULT, NoteColor.fromString(""))
    }

    @Test
    fun `NoteColor hexValue should be correct`() {
        assertEquals(0xFFFFFFFF, NoteColor.DEFAULT.hexValue)
        assertEquals(0xFFF4A44A, NoteColor.JOY.hexValue)
        assertEquals(0xFF3B82C4, NoteColor.MELANCHOLY.hexValue)
        assertEquals(0xFF2EC9A0, NoteColor.CALM.hexValue)
        assertEquals(0xFFE05FA0, NoteColor.ANGER.hexValue)
        assertEquals(0xFF7B5EA7, NoteColor.REFLECTIVE.hexValue)
    }

    @Test
    fun `NoteColor entries should have 6 values`() {
        assertEquals(6, NoteColor.entries.size)
    }
}

/**
 * Tests for EmotionSystem and EmotionCategory.
 */
class EmotionSystemTest {

    @Test
    fun `EmotionSystem should have 4 categories`() {
        assertEquals(4, EmotionSystem.categories.size)
    }

    @Test
    fun `EmotionSystem categories should have correct IDs`() {
        val ids = EmotionSystem.categories.map { it.id }
        assertTrue(ids.contains("HEU"))
        assertTrue(ids.contains("HEP"))
        assertTrue(ids.contains("LEP"))
        assertTrue(ids.contains("LEU"))
    }

    @Test
    fun `EmotionSystem HEU category should have correct properties`() {
        val heu = EmotionSystem.categories.find { it.id == "HEU" }
        assertNotNull(heu)
        assertEquals("High Energy, Unpleasant", heu.name)
        assertEquals("#FF5722", heu.color)
        assertEquals(5, heu.subEmotions.size)
        assertTrue(heu.subEmotions.contains("Agitated"))
        assertTrue(heu.subEmotions.contains("Furious"))
    }

    @Test
    fun `EmotionSystem HEP category should have correct properties`() {
        val hep = EmotionSystem.categories.find { it.id == "HEP" }
        assertNotNull(hep)
        assertEquals("High Energy, Pleasant", hep.name)
        assertEquals("#FFC107", hep.color)
        assertTrue(hep.subEmotions.contains("Lively"))
        assertTrue(hep.subEmotions.contains("Ecstatic"))
    }

    @Test
    fun `EmotionSystem LEP category should have correct properties`() {
        val lep = EmotionSystem.categories.find { it.id == "LEP" }
        assertNotNull(lep)
        assertEquals("Low Energy, Pleasant", lep.name)
        assertEquals("#4CAF50", lep.color)
        assertTrue(lep.subEmotions.contains("Relaxed"))
        assertTrue(lep.subEmotions.contains("Tranquil"))
    }

    @Test
    fun `EmotionSystem LEU category should have correct properties`() {
        val leu = EmotionSystem.categories.find { it.id == "LEU" }
        assertNotNull(leu)
        assertEquals("Low Energy, Unpleasant", leu.name)
        assertEquals("#3F51B5", leu.color)
        assertTrue(leu.subEmotions.contains("Disappointed"))
        assertTrue(leu.subEmotions.contains("Lethargic"))
    }

    @Test
    fun `getCategoryBySubEmotion should return correct category for HEU sub-emotion`() {
        val category = EmotionSystem.getCategoryBySubEmotion("Agitated")
        assertNotNull(category)
        assertEquals("HEU", category.id)
    }

    @Test
    fun `getCategoryBySubEmotion should return correct category for HEP sub-emotion`() {
        val category = EmotionSystem.getCategoryBySubEmotion("Enthusiastic")
        assertNotNull(category)
        assertEquals("HEP", category.id)
    }

    @Test
    fun `getCategoryBySubEmotion should return correct category for LEP sub-emotion`() {
        val category = EmotionSystem.getCategoryBySubEmotion("Peaceful")
        assertNotNull(category)
        assertEquals("LEP", category.id)
    }

    @Test
    fun `getCategoryBySubEmotion should return correct category for LEU sub-emotion`() {
        val category = EmotionSystem.getCategoryBySubEmotion("Gloomy")
        assertNotNull(category)
        assertEquals("LEU", category.id)
    }

    @Test
    fun `getCategoryBySubEmotion should return null for unknown sub-emotion`() {
        val category = EmotionSystem.getCategoryBySubEmotion("Unknown")
        assertNull(category)
    }

    @Test
    fun `getCategoryBySubEmotion should return null for null input`() {
        val category = EmotionSystem.getCategoryBySubEmotion(null)
        assertNull(category)
    }

    @Test
    fun `getCategoryBySubEmotion should return null for empty string`() {
        val category = EmotionSystem.getCategoryBySubEmotion("")
        assertNull(category)
    }

    @Test
    fun `EmotionCategory data class should have correct properties`() {
        val category = EmotionCategory(
            id = "TEST",
            name = "Test Category",
            color = "#000000",
            subEmotions = listOf("Emotion1", "Emotion2")
        )
        assertEquals("TEST", category.id)
        assertEquals("Test Category", category.name)
        assertEquals("#000000", category.color)
        assertEquals(2, category.subEmotions.size)
    }
}
