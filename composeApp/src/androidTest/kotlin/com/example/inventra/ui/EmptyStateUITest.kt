package com.example.inventra.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventra.presentation.components.EmptyState
import com.example.inventra.presentation.theme.InventRaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmptyStateUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // UI Test 6
    @Test
    fun emptyState_displaysCustomTitleAndDescription() {
        composeTestRule.setContent {
            InventRaTheme {
                EmptyState(
                    title = "Tidak Ada Barang",
                    description = "Belum ada barang dalam kategori ini"
                )
            }
        }

        composeTestRule.onNodeWithText("Tidak Ada Barang").assertIsDisplayed()
        composeTestRule.onNodeWithText("Belum ada barang dalam kategori ini").assertIsDisplayed()
    }

    // UI Test 7
    @Test
    fun emptyState_displaysDefaultTitleWhenNoArguments() {
        composeTestRule.setContent {
            InventRaTheme {
                EmptyState()
            }
        }

        // Default title dari parameter default EmptyState
        composeTestRule.onNodeWithText("Tidak Ada Data").assertIsDisplayed()
    }
}