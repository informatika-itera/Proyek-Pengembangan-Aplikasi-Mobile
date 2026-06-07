package com.example.todomaster

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThemeConfigTest {
    @Test
    fun `test theme toggle logic`() {
        ThemeConfig.isDarkTheme = true
        assertTrue(ThemeConfig.isDarkTheme)

        ThemeConfig.isDarkTheme = false
        assertFalse(ThemeConfig.isDarkTheme)
    }
}