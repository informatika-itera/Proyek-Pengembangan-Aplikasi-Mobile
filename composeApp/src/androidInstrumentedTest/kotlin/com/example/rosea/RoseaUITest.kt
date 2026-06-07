package com.example.rosea

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class RoseaUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testHomeScreenVisuals() {
        // Verifikasi elemen utama di Home
        composeTestRule.onNodeWithText("ROSÉA").assertExists()
        composeTestRule.onNodeWithText("Categories").assertExists()
        composeTestRule.onNodeWithText("Search your favorite products...").assertExists()
    }

    @Test
    fun testSearchFunctionality() {
        // Simulasi input pencarian
        val query = "Lipstick"
        composeTestRule.onNodeWithText("Search your favorite products...")
            .performTextInput(query)
        
        // Verifikasi teks terinput
        composeTestRule.onNodeWithText(query).assertExists()
    }

    @Test
    fun testEmptyStateVisibility() {
        // Mencari teks yang tidak mungkin ada untuk memicu Empty State
        composeTestRule.onNodeWithText("Search your favorite products...")
            .performTextInput("ZXZX123999")
        
        // Tunggu debounce dan cek placeholder
        composeTestRule.onNodeWithText("No products found").assertExists()
    }
}
