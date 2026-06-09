package com.example.fitkos

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import org.junit.Rule
import org.junit.Test

class FitKosUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun hasTag(tag: String): Boolean {
        return composeTestRule
            .onAllNodesWithTag(tag)
            .fetchSemanticsNodes()
            .isNotEmpty()
    }

    private fun waitForTag(tag: String, timeoutMillis: Long = 10_000) {
        composeTestRule.waitUntil(timeoutMillis = timeoutMillis) {
            hasTag(tag)
        }
        composeTestRule.waitForIdle()
    }

    private fun launchDashboardForTest() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_SKIP_SPLASH_FOR_UI_TEST, true)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        composeTestRule.activity.startActivity(intent)
        composeTestRule.waitForIdle()

        waitForTag("screen_dashboard")
    }

    @Test
    fun dashboard_isDisplayed() {
        launchDashboardForTest()

        composeTestRule
            .onNodeWithTag("screen_dashboard")
            .assertIsDisplayed()
    }

    @Test
    fun bottomNavigation_canOpenMealLog() {
        launchDashboardForTest()

        composeTestRule
            .onNodeWithTag("bottom_bar_Home")
            .performClick()

        waitForTag("screen_meal_log")

        composeTestRule
            .onNodeWithTag("screen_meal_log")
            .assertIsDisplayed()
    }

    @Test
    fun bottomNavigation_canOpenWaterTracker() {
        launchDashboardForTest()

        composeTestRule
            .onNodeWithTag("bottom_bar_WaterTracker")
            .performClick()

        waitForTag("screen_water_tracker")

        composeTestRule
            .onNodeWithTag("screen_water_tracker")
            .assertIsDisplayed()
    }
}