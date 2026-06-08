package com.example.travelplanner

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.travelplanner.core.util.LocalStrings
import com.example.travelplanner.core.util.StringsID
import com.example.travelplanner.presentation.components.ConnectivityBanner
import org.junit.Rule
import org.junit.Test
import androidx.compose.runtime.CompositionLocalProvider

class ConnectivityBannerUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testOfflineBannerShowsWhenOffline() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalStrings provides StringsID) {
                ConnectivityBanner(isOnline = false)
            }
        }

        // Verify that offline text is visible
        composeTestRule.onNodeWithText(StringsID.noInternetConnection).assertExists()
        composeTestRule.onNodeWithText(StringsID.someFeaturesUnavailable).assertExists()
    }
}
