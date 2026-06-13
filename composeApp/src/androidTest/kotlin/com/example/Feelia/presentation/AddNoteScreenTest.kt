package com.example.Feelia.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.Feelia.presentation.screens.addnote.AddNoteScreen
import com.example.Feelia.presentation.theme.FeeliaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
//import kotlin.test.assertTrue
import org.junit.Assert.assertTrue


@RunWith(AndroidJUnit4::class)
class AddNoteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ==================== LAYOUT ====================

    @Test
    fun addNoteScreen_showsJurnalBaruTitle() {
        composeTestRule.setContent {
            FeeliaTheme {
                AddNoteScreen(
                    noteId = null,
                    onNavigateBack = {},
                    onNavigateToAI = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Jurnal Baru")
            .assertIsDisplayed()
    }

    @Test
    fun addNoteScreen_showsMotivationCard() {
        composeTestRule.setContent {
            FeeliaTheme {
                AddNoteScreen(
                    noteId = null,
                    onNavigateBack = {},
                    onNavigateToAI = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Bagaimana harimu?")
            .assertIsDisplayed()
    }

    @Test
    fun addNoteScreen_showsInputPlaceholder() {
        composeTestRule.setContent {
            FeeliaTheme {
                AddNoteScreen(
                    noteId = null,
                    onNavigateBack = {},
                    onNavigateToAI = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Hari ini aku merasa...")
            .assertIsDisplayed()
    }

    // ==================== NAVIGATION ====================

    @Test
    fun backButton_onClick_navigatesBack() {
        var navigatedBack = false

        composeTestRule.setContent {
            FeeliaTheme {
                AddNoteScreen(
                    noteId = null,
                    onNavigateBack = { navigatedBack = true },
                    onNavigateToAI = {}
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Kembali")
            .performClick()

        assertTrue(navigatedBack)
    }

    // ==================== INPUT ====================

    @Test
    fun charCounter_showsZeroInitially() {
        composeTestRule.setContent {
            FeeliaTheme {
                AddNoteScreen(
                    noteId = null,
                    onNavigateBack = {},
                    onNavigateToAI = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("0/500")
            .assertIsDisplayed()
    }

    @Test
    fun textInput_updatesCharCounter() {
        composeTestRule.setContent {
            FeeliaTheme {
                AddNoteScreen(
                    noteId = null,
                    onNavigateBack = {},
                    onNavigateToAI = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Hari ini aku merasa...")
            .performTextInput("Hari ini menyenangkan")

        composeTestRule
            .onNodeWithText("21/500")
            .assertIsDisplayed()
    }
}