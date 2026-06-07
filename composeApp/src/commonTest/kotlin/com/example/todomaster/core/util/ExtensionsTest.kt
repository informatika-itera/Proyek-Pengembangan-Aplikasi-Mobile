package com.example.todomaster.core.util

import kotlin.test.Test
import kotlin.test.assertEquals

class ExtensionsTest {
    @Test
    fun `test string extensions`() {
        assertEquals("Halo...", "Halo Dunia".truncate(7))
        assertEquals("Halo", "halo".capitalizeFirst())
    }
}