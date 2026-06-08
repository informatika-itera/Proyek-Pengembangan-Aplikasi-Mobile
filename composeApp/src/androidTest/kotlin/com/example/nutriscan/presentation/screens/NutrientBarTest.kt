package com.example.nutriscan.presentation.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.nutriscan.domain.model.NutritionStatus
import com.example.nutriscan.presentation.components.NutrientBar
import com.example.nutriscan.presentation.theme.NutriScanTheme
import org.junit.Rule
import org.junit.Test

class NutrientBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun nutrientBar_tampilLabelDanNilai() {
        composeTestRule.setContent {
            NutriScanTheme {
                NutrientBar(
                    label        = "Gula",
                    value        = 10f,
                    unit         = "g",
                    percentDaily = 40f,
                    status       = NutritionStatus.AVOID
                )
            }
        }
        composeTestRule.onNodeWithText("Gula").assertIsDisplayed()
        composeTestRule.onNodeWithText("10 g  (40% AKG)", substring = true).assertIsDisplayed()
    }

    @Test
    fun nutrientBar_percentMelebihi100_tidakCrash() {
        composeTestRule.setContent {
            NutriScanTheme {
                NutrientBar(
                    label        = "Kalori",
                    value        = 500f,
                    unit         = "kcal",
                    percentDaily = 150f,   // > 100%
                    status       = NutritionStatus.AVOID
                )
            }
        }
        composeTestRule.onNodeWithText("Kalori").assertIsDisplayed()
    }
}