package com.example.rewind

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.rewind.presentation.screens.addmovie.AddMovieScreen
import com.example.rewind.presentation.theme.RewindTheme
import org.junit.Rule
import org.junit.Test

class AddMovieScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addMovieScreen_showsErrorWhenTitleEmpty() {
        composeTestRule.setContent {
            RewindTheme {
                AddMovieScreen(
                    movieId = null,
                    onNavigateBack = {}
                )
            }
        }

        // Klik tombol save tanpa isi title
        composeTestRule
            .onNodeWithText("Save to Collection")
            .performClick()

        // Error message harus muncul
        composeTestRule
            .onNodeWithText("Judul tidak boleh kosong")
            .assertIsDisplayed()
    }

    @Test
    fun addMovieScreen_canInputTitleAndSave() {
        composeTestRule.setContent {
            RewindTheme {
                AddMovieScreen(
                    movieId = null,
                    onNavigateBack = {}
                )
            }
        }

        // Input judul
        composeTestRule
            .onNodeWithText("Movie or series title...")
            .performTextInput("Inception")

        // Tombol save harus ada
        composeTestRule
            .onNodeWithText("Save to Collection")
            .assertIsDisplayed()
    }
}