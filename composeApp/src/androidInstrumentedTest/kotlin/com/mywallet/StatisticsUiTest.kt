package com.mywallet

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class StatisticsUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testNavigateToStatistics_showsAnalysisTitle() {
        composeTestRule.onNodeWithContentDescription("Statistik").performClick()
        composeTestRule.onNodeWithText("Analisis Keuangan").assertIsDisplayed()
    }
}
