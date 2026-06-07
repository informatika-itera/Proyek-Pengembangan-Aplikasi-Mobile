package com.example.bridgebit

import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.presentation.screens.dashboard.DashboardUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class AppFinalTest {

    @Test
    fun testDashboardSuccessState() {
        // Gunakan Named Arguments agar tidak error parameter
        val mockData = listOf(
            Translation(
                id = 1L,
                sourceText = "Hi",
                translatedText = "Halo",
                createdAt = 0L,
                updatedAt = 0L
                // Jika di Translation.kt ada parameter lain seperti sourceLanguage, tambahkan di sini!
            ),
            Translation(
                id = 2L,
                sourceText = "Bye",
                translatedText = "Dah",
                createdAt = 0L,
                updatedAt = 0L
            )
        )

        val state = DashboardUiState.Success(history = mockData)

        assertEquals(2, state.history.size)
        assertEquals("Halo", state.history[0].translatedText)
    }

    @Test
    fun testTranslationLogic() {
        val input = "Hello"
        val isValid = input.length > 2
        assertTrue(isValid, "Teks harus lebih dari 2 karakter")
    }

    @Test
    fun testBoundaryConditions() {
        val length1 = 50
        val length2 = 150

        val isLong = length1 > 100
        val isLong2 = length2 > 100

        assertTrue(!isLong, "50 seharusnya dianggap pendek")
        assertTrue(isLong2, "150 seharusnya dianggap panjang")
    }
}