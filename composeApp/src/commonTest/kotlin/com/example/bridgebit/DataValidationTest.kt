package com.example.bridgebit

import com.example.bridgebit.domain.model.Translation
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class DataValidationTest {

    @Test
    fun testPreview_exactly100Chars_doesNotTruncate() {
        val exact100 = "A".repeat(100)

        // SESUAIKAN URUTAN PARAMETER sesuai dengan class Translation Anda
        val translation = Translation(
            id = 1L,
            sourceText = "Test",
            translatedText = exact100,
            createdAt = 0L,
            updatedAt = 0L
        )

        assertEquals(exact100, translation.preview)
    }

    @Test
    fun testTranslation_invalidData_isHandled() {
        // SESUAIKAN URUTAN PARAMETER
        val translation = Translation(
            id = 99L,
            sourceText = "!!??@@",
            translatedText = "!!??@@",
            createdAt = 0L,
            updatedAt = 0L
        )
        assertFalse(translation.isEmpty)
    }
}