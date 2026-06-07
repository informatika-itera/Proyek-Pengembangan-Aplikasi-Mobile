package com.example.bridgebit

import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.presentation.screens.dashboard.DashboardUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DashboardStateTest {

    // Test 1: Memastikan UI State Loading siap digunakan tanpa error
    @Test
    fun testDashboardState_Loading_isCorrectlyInstantiated() {
        // Arrange & Act
        val state: DashboardUiState = DashboardUiState.Loading

        // Assert
        assertTrue(state is DashboardUiState.Loading, "State harus berupa Loading")
    }

    // Test 2: Memastikan UI State Empty siap digunakan saat tidak ada riwayat
    @Test
    fun testDashboardState_Empty_isCorrectlyInstantiated() {
        // Arrange & Act
        val state: DashboardUiState = DashboardUiState.Empty

        // Assert
        assertTrue(state is DashboardUiState.Empty, "State harus berupa Empty")
    }

    // Test 3: Memastikan UI State Success mampu menyimpan dan membawa data terjemahan ke UI
    @Test
    fun testDashboardState_Success_holdsDataCorrectly() {
        // Arrange: Siapkan data riwayat terjemahan palsu (mock data)
        val mockHistory = listOf(
            Translation(
                id = 1L,
                sourceText = "Good morning",
                translatedText = "Selamat pagi",
                sourceLanguage = "en",
                targetLanguage = "id",
                createdAt = 12345L,
                updatedAt = 12345L
            )
        )

        // Act: Masukkan data tersebut ke dalam State Success milik Dashboard
        val state = DashboardUiState.Success(history = mockHistory)

        // Assert: Verifikasi bahwa UI State membawa data yang tepat untuk ditampilkan di layar
        assertEquals(1, state.history.size)
        assertEquals("Good morning", state.history[0].sourceText)
        assertEquals("Selamat pagi", state.history[0].translatedText)
    }
}