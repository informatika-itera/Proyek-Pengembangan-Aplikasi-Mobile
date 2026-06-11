package com.example.inventra.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventra.presentation.screens.catalog.CatalogScreen
import com.example.inventra.presentation.theme.InventRaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatalogUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun catalogScreen_searchFieldIsDisplayed() {
        composeTestRule.setContent {
            InventRaTheme {
                CatalogScreen(
                    currentRoute = "catalog",
                    onNavigate = {},
                    onNavigateToDetail = {},
                    onNavigateToAddItem = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Cari barang...").assertIsDisplayed()
    }

    @Test
    fun catalogScreen_categoryChipsAreDisplayed() {
        composeTestRule.setContent {
            InventRaTheme {
                CatalogScreen(
                    currentRoute = "catalog",
                    onNavigate = {},
                    onNavigateToDetail = {},
                    onNavigateToAddItem = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Medis").assertIsDisplayed()
        composeTestRule.onNodeWithText("Elektronik").assertIsDisplayed()
    }
}
