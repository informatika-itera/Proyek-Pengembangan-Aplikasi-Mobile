package com.mywallet

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class HomeScreenUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAppLaunch_showsMyWalletTitle() {
        // Wait for Splash animation if any
        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText("MyWallet").fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule.onNodeWithText("MyWallet").assertIsDisplayed()
    }

    @Test
    fun testNavigateToHistory_showsHistoryTitle() {
        composeTestRule.onNodeWithContentDescription("Riwayat").performClick()
        composeTestRule.onNodeWithText("Riwayat Transaksi").assertIsDisplayed()
    }

    @Test
    fun testNavigateToProfile_showsProfileTitle() {
        composeTestRule.onNodeWithContentDescription("Profil").performClick()
        composeTestRule.onNodeWithText("Profil Saya").assertIsDisplayed()
    }
}
