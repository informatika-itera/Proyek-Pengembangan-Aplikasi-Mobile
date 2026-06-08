package com.example.nutriscan.presentation.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.nutriscan.domain.model.Nutriments
import com.example.nutriscan.domain.model.NutritionAnalysis
import com.example.nutriscan.domain.model.NutritionStatus
import com.example.nutriscan.domain.model.Product
import com.example.nutriscan.domain.model.ScanResult
import com.example.nutriscan.presentation.components.StatusChip
import com.example.nutriscan.presentation.theme.NutriScanTheme
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class StatusChipTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun statusChip_safe_tampilTeksAman() {
        composeTestRule.setContent {
            NutriScanTheme {
                StatusChip(status = NutritionStatus.SAFE)
            }
        }
        composeTestRule
            .onNodeWithText("✅ Aman")
            .assertIsDisplayed()
    }

    @Test
    fun statusChip_caution_tampilTeksPerhatian() {
        composeTestRule.setContent {
            NutriScanTheme {
                StatusChip(status = NutritionStatus.CAUTION)
            }
        }
        composeTestRule
            .onNodeWithText("⚠️ Perhatian")
            .assertIsDisplayed()
    }

    @Test
    fun statusChip_avoid_tampilTeksHindari() {
        composeTestRule.setContent {
            NutriScanTheme {
                StatusChip(status = NutritionStatus.AVOID)
            }
        }
        composeTestRule
            .onNodeWithText("🚫 Hindari")
            .assertIsDisplayed()
    }
}