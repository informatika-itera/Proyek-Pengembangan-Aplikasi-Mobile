package com.example.bridgebit

import com.example.bridgebit.domain.model.Translation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TranslationTest {

    // Test 1: Menguji apakah teks pendek ditampilkan secara utuh pada preview
    @Test
    fun testPreview_withShortText_returnsFullText() {
        // Persiapan Data (Arrange)
        val translation = Translation(
            id = 1L,
            sourceText = "Hello World",
            translatedText = "Halo Dunia",
            createdAt = 0L,
            updatedAt = 0L
        )

        // Eksekusi & Verifikasi (Act & Assert)
        assertEquals("Halo Dunia", translation.preview)
    }

    // Test 2: Menguji apakah teks panjang (>100 karakter) dipotong dan ditambah "..."
    @Test
    fun testPreview_withLongText_returnsTruncatedText() {
        // Persiapan Data: Membuat teks sepanjang 120 karakter
        val longTranslatedText = "A".repeat(120)
        val translation = Translation(
            id = 2L,
            sourceText = "Very long text",
            translatedText = longTranslatedText,
            createdAt = 0L,
            updatedAt = 0L
        )

        // Eksekusi & Verifikasi
        val expectedPreview = "A".repeat(100) + "..."
        assertEquals(expectedPreview, translation.preview)
    }

    // Test 3: Menguji validasi pengecekan apakah terjemahan dianggap kosong
    @Test
    fun testIsEmpty_withBlankTexts_returnsTrue() {
        // Persiapan Data: Membuat teks sumber hanya berisi spasi dan hasil kosong
        val translation = Translation(
            id = 3L,
            sourceText = "   ",
            translatedText = "",
            createdAt = 0L,
            updatedAt = 0L
        )

        // Eksekusi & Verifikasi
        assertTrue(translation.isEmpty)
    }
}