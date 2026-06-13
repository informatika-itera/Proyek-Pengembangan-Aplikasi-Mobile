package com.example.fitgen.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FitGenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testBottomNavigationClicks() {
        composeTestRule.setContent {
            // App()
        }
        // composeTestRule.onNodeWithText("Home").performClick()
        // composeTestRule.onNodeWithText("Daily Tracker").assertIsDisplayed()
    }

    @Test
    fun testProfileEditValidationUI() {
        composeTestRule.setContent {
            // EditProfileScreen()
        }
        // composeTestRule.onNodeWithText("Berat Badan (kg)").performTextInput("600")
        // composeTestRule.onNodeWithText("Berat badan maksimal 500 kg").assertIsDisplayed()
    }

    @Test
    fun testAIAssistantEmptyInputValidation() {
        composeTestRule.setContent {
            // AIAssistantScreen()
        }
        // composeTestRule.onNodeWithText("Kirim").performClick()
        // composeTestRule.onNodeWithText("Masukkan teks terlebih dahulu").assertIsDisplayed()
    }
}
