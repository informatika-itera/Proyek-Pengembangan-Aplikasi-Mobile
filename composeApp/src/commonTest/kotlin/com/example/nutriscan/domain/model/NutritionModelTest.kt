package com.example.nutriscan.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NutritionModelTest {

    @Test
    fun `NutritionStatus fromString mengembalikan status valid`() {
        assertEquals(NutritionStatus.SAFE, NutritionStatus.fromString("SAFE"))
        assertEquals(NutritionStatus.CAUTION, NutritionStatus.fromString("CAUTION"))
        assertEquals(NutritionStatus.AVOID, NutritionStatus.fromString("AVOID"))
    }

    @Test
    fun `NutritionStatus fromString fallback ke SAFE jika value salah`() {
        assertEquals(NutritionStatus.SAFE, NutritionStatus.fromString("SALAH"))
    }

    @Test
    fun `warningMessages hanya menampilkan warning tidak safe`() {
        val analysis = NutritionAnalysis(
            overallStatus = NutritionStatus.CAUTION,
            warnings = listOf(
                NutrientWarning(
                    nutrientName = "Gula",
                    valuePerServing = 12.5f,
                    unit = "g",
                    percentDailyValue = 50f,
                    status = NutritionStatus.CAUTION
                ),
                NutrientWarning(
                    nutrientName = "Natrium",
                    valuePerServing = 50f,
                    unit = "mg",
                    percentDailyValue = 2f,
                    status = NutritionStatus.SAFE
                )
            )
        )

        val messages = analysis.warningMessages

        assertEquals(1, messages.size)
        assertTrue(messages.first().contains("Gula"))
        assertTrue(messages.first().contains("Perhatian"))
    }

    @Test
    fun `warningMessages melakukan format angka satu desimal`() {
        val analysis = NutritionAnalysis(
            overallStatus = NutritionStatus.AVOID,
            warnings = listOf(
                NutrientWarning(
                    nutrientName = "Gula",
                    valuePerServing = 12.34f,
                    unit = "g",
                    percentDailyValue = 40f,
                    status = NutritionStatus.AVOID
                )
            )
        )

        val message = analysis.warningMessages.first()

        assertTrue(message.contains("12.3g"))
    }
}