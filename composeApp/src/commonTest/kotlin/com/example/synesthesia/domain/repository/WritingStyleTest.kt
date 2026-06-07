package com.example.synesthesia.domain.repository

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for WritingStyle enum to ensure full coverage of the enum values,
 * display names, and prompts.
 */
class WritingStyleTest {

    @Test
    fun `WritingStyle should have 5 entries`() {
        assertEquals(5, WritingStyle.entries.size)
    }

    @Test
    fun `NEUTRAL should have correct displayName and prompt`() {
        assertEquals("Netral", WritingStyle.NEUTRAL.displayName)
        assertTrue(WritingStyle.NEUTRAL.prompt.contains("netral"))
    }

    @Test
    fun `FORMAL should have correct displayName and prompt`() {
        assertEquals("Formal", WritingStyle.FORMAL.displayName)
        assertTrue(WritingStyle.FORMAL.prompt.contains("formal"))
    }

    @Test
    fun `CASUAL should have correct displayName and prompt`() {
        assertEquals("Kasual", WritingStyle.CASUAL.displayName)
        assertTrue(WritingStyle.CASUAL.prompt.contains("santai"))
    }

    @Test
    fun `ACADEMIC should have correct displayName and prompt`() {
        assertEquals("Akademik", WritingStyle.ACADEMIC.displayName)
        assertTrue(WritingStyle.ACADEMIC.prompt.contains("akademik"))
    }

    @Test
    fun `CREATIVE should have correct displayName and prompt`() {
        assertEquals("Kreatif", WritingStyle.CREATIVE.displayName)
        assertTrue(WritingStyle.CREATIVE.prompt.contains("kreatif"))
    }

    @Test
    fun `all WritingStyle values should have non-empty displayName`() {
        WritingStyle.entries.forEach { style ->
            assertTrue(style.displayName.isNotBlank(), "displayName for ${style.name} should not be blank")
        }
    }

    @Test
    fun `all WritingStyle values should have non-empty prompt`() {
        WritingStyle.entries.forEach { style ->
            assertTrue(style.prompt.isNotBlank(), "prompt for ${style.name} should not be blank")
        }
    }
}
