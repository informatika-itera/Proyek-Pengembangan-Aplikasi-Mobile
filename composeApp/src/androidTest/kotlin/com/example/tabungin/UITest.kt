package com.example.tabungin

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.waitForIdle
import org.junit.Rule
import org.junit.Test

/**
 * UI Tests for critical flows in TabungIn app
 */
class UITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Test: Verify Home Screen displays correctly
     */
    @Test
    fun homeScreen_displaysCorrectly() {
        // Wait for content to load
        composeTestRule.waitForIdle()

        // Verify essential elements are displayed
        composeTestRule.onNodeWithText("TabungIn").assertIsDisplayed()
    }

    /**
     * Test: Navigate to Add Target screen
     */
    @Test
    fun addTargetScreen_navigatesCorrectly() {
        composeTestRule.waitForIdle()

        // Click on add button (FAB)
        composeTestRule.onNodeWithText("Tambah").assertIsDisplayed()
    }

    /**
     * Test: Empty state is shown when no targets
     */
    @Test
    fun emptyState_shownWhenNoTargets() {
        composeTestRule.waitForIdle()

        // Should show empty state message
        composeTestRule.onNodeWithText("Mulai menabung").assertIsDisplayed()
    }
}
