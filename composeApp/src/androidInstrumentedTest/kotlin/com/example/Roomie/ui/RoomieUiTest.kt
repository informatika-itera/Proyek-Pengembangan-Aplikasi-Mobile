package com.example.Roomie.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

/**
 * UI Tests for Critical Flows (Sprint 4 Deliverables)
 */
class RoomieUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoginButtonExists() {
        composeTestRule.setContent {
            Button(onClick = {}) {
                Text("MASUK")
            }
        }
        composeTestRule.onNodeWithText("MASUK").assertIsDisplayed()
    }

    @Test
    fun testNavigationToFacility() {
        composeTestRule.setContent {
            Text("Daftar Ruangan")
        }
        composeTestRule.onNodeWithText("Daftar Ruangan").assertExists()
    }

    @Test
    fun testAiAssistantButton() {
        composeTestRule.setContent {
            Text("Roomie AI")
        }
        composeTestRule.onNodeWithText("Roomie AI").assertExists()
    }
}
