package com.example.travelplanner

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.travelplanner.core.util.LocalStrings
import com.example.travelplanner.core.util.StringsID
import com.example.travelplanner.presentation.screens.home.HeroSection
import com.example.travelplanner.presentation.screens.home.InsightBanner
import org.junit.Rule
import org.junit.Test
import androidx.compose.runtime.CompositionLocalProvider

class HomeScreenUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testHeroSectionLayout() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalStrings provides StringsID) {
                HeroSection()
            }
        }

        // Verify that heading labels exist
        composeTestRule.onNodeWithText("AI TRAVEL PLANNER").assertExists()
        composeTestRule.onNodeWithText("Traveler.").assertExists()
    }

    @Test
    fun testInsightBannerLayout() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalStrings provides StringsID) {
                InsightBanner()
            }
        }

        // Verify that tips header is shown
        composeTestRule.onNodeWithText(StringsID.tipTitle).assertExists()
        composeTestRule.onNodeWithText(StringsID.tipBody).assertExists()
    }
}
