package com.example.nutriscan.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DiseaseTest {

    @Test
    fun `fromString mengembalikan disease valid`() {
        assertEquals(Disease.DIABETES, Disease.fromString("DIABETES"))
        assertEquals(Disease.HYPERTENSION, Disease.fromString("HYPERTENSION"))
    }

    @Test
    fun `fromString mengembalikan null untuk value tidak valid`() {
        assertNull(Disease.fromString("UNKNOWN"))
    }

    @Test
    fun `fromCsv mengubah csv menjadi list disease`() {
        val result = Disease.fromCsv("DIABETES,HYPERTENSION,OBESITY")

        assertEquals(
            listOf(Disease.DIABETES, Disease.HYPERTENSION, Disease.OBESITY),
            result
        )
    }

    @Test
    fun `fromCsv mengabaikan value kosong dan tidak valid`() {
        val result = Disease.fromCsv("DIABETES, ,SALAH,HYPERTENSION")

        assertEquals(
            listOf(Disease.DIABETES, Disease.HYPERTENSION),
            result
        )
    }

    @Test
    fun `toCsv mengubah list disease menjadi string`() {
        val csv = Disease.toCsv(
            listOf(Disease.DIABETES, Disease.OBESITY)
        )

        assertEquals("DIABETES,OBESITY", csv)
    }
}