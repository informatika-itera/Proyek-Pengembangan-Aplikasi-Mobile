package com.example.mapenumkm.core.util

import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExtensionsTest {

    @Test
    fun `String truncate should shorten long strings`() {
        val longString = "This is a very long string that needs truncation"
        val truncated = longString.truncate(10)
        assertEquals("This is...", truncated)
        assertEquals(10, truncated.length)
    }

    @Test
    fun `String truncate should not change short strings`() {
        val shortString = "Short"
        assertEquals(shortString, shortString.truncate(10))
    }

    @Test
    fun `capitalizeFirst should capitalize only the first char`() {
        assertEquals("Hello", "hello".capitalizeFirst())
        assertEquals("Test", "Test".capitalizeFirst())
    }

    @Test
    fun `Instant formatters should return non-empty strings`() {
        val now = Instant.fromEpochMilliseconds(0)
        assertTrue(now.formatToDisplay().isNotEmpty())
        assertTrue(now.formatDateOnly().isNotEmpty())
        assertTrue(now.formatTimeOnly().isNotEmpty())
    }
}
