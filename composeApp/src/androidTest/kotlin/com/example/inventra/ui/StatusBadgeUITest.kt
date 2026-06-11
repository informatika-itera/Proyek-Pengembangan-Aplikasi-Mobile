package com.example.inventra.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventra.presentation.components.StatusBadge
import com.example.inventra.presentation.theme.InventRaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatusBadgeUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // UI Test 8
    @Test
    fun statusBadge_displaysAvailableStatusUppercase() {
        composeTestRule.setContent {
            InventRaTheme {
                StatusBadge(
                    status = "Available",
                    isAvailable = true
                )
            }
        }

        composeTestRule.onNodeWithText("AVAILABLE").assertIsDisplayed()
    }

    // UI Test 9
    @Test
    fun statusBadge_displaysBorrowedStatus() {
        composeTestRule.setContent {
            InventRaTheme {
                StatusBadge(
                    status = "Borrowed",
                    isAvailable = false
                )
            }
        }

        composeTestRule.onNodeWithText("BORROWED").assertIsDisplayed()
    }

    // UI Test 10
    @Test
    fun statusBadge_displaysCustomStatusText() {
        composeTestRule.setContent {
            InventRaTheme {
                StatusBadge(
                    status = "Overdue",
                    isAvailable = false
                )
            }
        }

        composeTestRule.onNodeWithText("OVERDUE").assertIsDisplayed()
    }
}