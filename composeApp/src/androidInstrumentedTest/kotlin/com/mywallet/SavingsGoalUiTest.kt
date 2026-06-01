package com.mywallet

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class SavingsGoalUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testNavigateToSavingsGoal_showsSavingsTitle() {
        // Wait for Splash
        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText("MyWallet").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithContentDescription("Target").performClick()
        composeTestRule.onNodeWithText("Target Tabungan").assertIsDisplayed()
    }

    @Test
    fun testAddSavingsGoal_showsDialog() {
        composeTestRule.onNodeWithContentDescription("Target").performClick()
        composeTestRule.onNodeWithContentDescription("Tambah Target").performClick()
        composeTestRule.onNodeWithText("Target Tabungan Baru").assertIsDisplayed()
    }
}
