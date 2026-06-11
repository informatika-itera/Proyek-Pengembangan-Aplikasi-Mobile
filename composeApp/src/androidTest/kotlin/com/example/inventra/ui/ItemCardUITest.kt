package com.example.inventra.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventra.presentation.components.ItemCard
import com.example.inventra.presentation.theme.InventRaTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ItemCardUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // UI Test 1
    @Test
    fun itemCard_displaysItemTitleAndCategory() {
        composeTestRule.setContent {
            InventRaTheme {
                ItemCard(
                    title = "Projector HMIF",
                    category = "Elektronik",
                    description = "Untuk presentasi",
                    stock = 3,
                    isAvailable = true,
                    onClick = {},
                    onBorrowClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Projector HMIF").assertIsDisplayed()
        composeTestRule.onNodeWithText("Elektronik").assertIsDisplayed()
    }

    // UI Test 2
    @Test
    fun itemCard_showsCorrectStockNumber() {
        composeTestRule.setContent {
            InventRaTheme {
                ItemCard(
                    title = "HT",
                    category = "Elektronik",
                    description = "Handy Talky",
                    stock = 11,
                    isAvailable = true,
                    onClick = {},
                    onBorrowClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("11 Unit").assertIsDisplayed()
    }

    // UI Test 3
    @Test
    fun itemCard_borrowButtonDisabledWhenStockZero() {
        composeTestRule.setContent {
            InventRaTheme {
                ItemCard(
                    title = "Speaker",
                    category = "Elektronik",
                    description = "Speaker portable",
                    stock = 0,
                    isAvailable = false,
                    onClick = {},
                    onBorrowClick = {}
                )
            }
        }

        // Ketika stok 0, teks tombol berubah menjadi "Habis" (strings.outOfStock)
        composeTestRule.onNodeWithText("Habis").assertIsNotEnabled()
    }

    // UI Test 4
    @Test
    fun itemCard_borrowButtonEnabledWhenStockAvailable() {
        composeTestRule.setContent {
            InventRaTheme {
                ItemCard(
                    title = "Mic",
                    category = "Elektronik",
                    description = "Microphone",
                    stock = 2,
                    isAvailable = true,
                    onClick = {},
                    onBorrowClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Pinjam").assertIsEnabled()
    }

    // UI Test 5
    @Test
    fun itemCard_onClickTriggeredWhenTapped() {
        var wasClicked = false

        composeTestRule.setContent {
            InventRaTheme {
                ItemCard(
                    title = "TOA",
                    category = "Elektronik",
                    description = "Pengeras suara",
                    stock = 1,
                    isAvailable = true,
                    onClick = { wasClicked = true },
                    onBorrowClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("TOA").performClick()
        assertTrue(wasClicked)
    }
}