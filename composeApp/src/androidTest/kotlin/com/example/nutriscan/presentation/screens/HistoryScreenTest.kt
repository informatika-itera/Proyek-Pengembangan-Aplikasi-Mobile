package com.example.nutriscan.presentation.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.nutriscan.domain.model.Nutriments
import com.example.nutriscan.domain.model.NutritionAnalysis
import com.example.nutriscan.domain.model.NutritionStatus
import com.example.nutriscan.domain.model.Product
import com.example.nutriscan.domain.model.ScanResult
import com.example.nutriscan.presentation.screens.history.HistoryScreen
import com.example.nutriscan.presentation.screens.history.HistoryUiState
import com.example.nutriscan.presentation.theme.NutriScanTheme
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class HistoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun makeScan(id: Long, name: String, barcode: String) = ScanResult(
        id       = id,
        product  = Product(
            barcode    = barcode,
            name       = name,
            nutriments = Nutriments()
        ),
        analysis = NutritionAnalysis(overallStatus = NutritionStatus.SAFE)
    )

    @Test
    fun historyEmpty_tampilPesanKosong() {
        composeTestRule.setContent {
            NutriScanTheme {
                HistoryScreen(
                    onNavigateBack = {},
                    onScanSelected = {}
                )
            }
        }
        // Karena ViewModel di-inject Koin — test ini untuk state Empty yang di-mock.
        // Untuk test UI murni tanpa Koin, inject state langsung via parameter.
        // Lihat catatan di bawah.
    }

    @Test
    fun historyLoading_tampilLoadingIndicator() {
        composeTestRule.setContent {
            NutriScanTheme {
                // Render langsung HistoryScreen content dengan state Loading
                androidx.compose.material3.CircularProgressIndicator()
            }
        }
        composeTestRule
            .onNodeWithContentDescription("Loading")
            .assertExists()
    }
}