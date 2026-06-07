package com.example.synesthesia

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performTextInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import com.example.synesthesia.presentation.screens.addnote.JournalingStep
import com.example.synesthesia.presentation.screens.home.FilterSortBottomSheet
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.usecase.NoteSortBy
import com.example.synesthesia.presentation.components.EmptyStateView
import com.example.synesthesia.presentation.theme.NoteAITheme
import com.example.synesthesia.presentation.theme.ThemeMode
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertTrue

class UITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyStateView_displaysCorrectTextAndIcon() {
        composeTestRule.setContent {
            NoteAITheme {
                EmptyStateView(
                    title = "No Journal Entries",
                    description = "Start writing your first galaxy note.",
                    icon = Icons.Default.Star
                )
            }
        }
        
        composeTestRule.onNodeWithText("No Journal Entries").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start writing your first galaxy note.").assertIsDisplayed()
    }

    @Test
    fun journalingStep_displaysInputFieldAndHandlesInput() {
        var content = ""
        composeTestRule.setContent {
            NoteAITheme {
                JournalingStep(
                    content = content,
                    onContentChange = { content = it },
                    isParaphraseEnabled = true,
                    onParaphraseToggle = {},
                    isAnalyzing = false,
                    isSaving = false
                )
            }
        }

        // Verify the placeholder text is present
        composeTestRule.onNodeWithText("Write your soul here...").assertIsDisplayed()
        
        // Enter some text
        composeTestRule.onNodeWithText("Write your soul here...").performTextInput("Hello Galaxy")
        
        // Due to the way state hoisting works in tests without recomposition loops in simple vars,
        // we just verify that the input action was executed successfully
        assertTrue(content == "Hello Galaxy")
    }

    @Test
    fun filterSortBottomSheet_displaysOptionsAndHandlesClick() {
        var categorySelected: NoteCategory? = null
        composeTestRule.setContent {
            NoteAITheme {
                FilterSortBottomSheet(
                    selectedCategory = null,
                    selectedSort = NoteSortBy.UPDATED_DESC,
                    onCategorySelected = { categorySelected = it },
                    onSortByChanged = {},
                    onDismiss = {}
                )
            }
        }

        // Check if bottom sheet options exist
        composeTestRule.onNodeWithText("Filter Memories").assertIsDisplayed()
        composeTestRule.onNodeWithText("By Category").assertIsDisplayed()
        
        // Find category chips and click one
        composeTestRule.onNodeWithText(NoteCategory.PERSONAL.displayName).assertIsDisplayed().performClick()
        
        // Assert action fired
        assertTrue(categorySelected == NoteCategory.PERSONAL)
    }

    @Test
    fun e2e_simulateMemoryCreationFlow() {
        // This is a partial E2E test as we are testing the component interaction logic
        // in a real scenario you would set up the whole screen with a fake repository.
        var content = ""
        var finishClicked = false

        composeTestRule.setContent {
            NoteAITheme {
                JournalingStep(
                    content = content,
                    onContentChange = { content = it },
                    isParaphraseEnabled = true,
                    onParaphraseToggle = {},
                    isAnalyzing = false,
                    isSaving = false
                )
            }
        }

        // 1. Input text
        composeTestRule.onNodeWithText("Write your soul here...").performTextInput("Memories of a shooting star")
        
        // 2. Validate input state
        assertTrue(content == "Memories of a shooting star")
    }
}
