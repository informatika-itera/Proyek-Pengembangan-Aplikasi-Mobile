package com.example.arcane.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.arcane.domain.model.Book
import com.example.arcane.domain.model.ReadingStatus
import com.example.arcane.presentation.screens.home.HomeScreenContent
import com.example.arcane.presentation.screens.home.HomeUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createBook(
        id: Long,
        title: String,
        status: ReadingStatus = ReadingStatus.TO_READ
    ) = Book(
        id = id,
        googleBookId = "google_$id",
        title = title,
        authors = listOf("Author Test"),
        description = "",
        coverUrl = "",
        categories = emptyList(),
        publishedDate = "",
        pageCount = null,
        readingStatus = status,
        notes = "",
        rating = null
    )

    @Test
    fun emptyState_showsRakBukuKosong() {
        composeTestRule.setContent {
            MaterialTheme {
                HomeScreenContent(
                    uiState = HomeUiState.Empty,
                    searchQuery = "",
                    selectedStatus = null,
                    onSearchQueryChange = {},
                    onStatusSelected = {},
                    onNavigateToExplore = {},
                    onNavigateToBook = { _, _ -> }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Rak Buku Kosong")
            .assertIsDisplayed()
    }

    @Test
    fun successState_showsBookTitle() {
        val books = listOf(createBook(1L, "Kotlin Programming"))

        composeTestRule.setContent {
            MaterialTheme {
                HomeScreenContent(
                    uiState = HomeUiState.Success(books),
                    searchQuery = "",
                    selectedStatus = null,
                    onSearchQueryChange = {},
                    onStatusSelected = {},
                    onNavigateToExplore = {},
                    onNavigateToBook = { _, _ -> }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Kotlin Programming")
            .assertIsDisplayed()
    }

    @Test
    fun fabClick_triggersNavigateToExplore() {
        var clicked = false

        composeTestRule.setContent {
            MaterialTheme {
                HomeScreenContent(
                    uiState = HomeUiState.Empty,
                    searchQuery = "",
                    selectedStatus = null,
                    onSearchQueryChange = {},
                    onStatusSelected = {},
                    onNavigateToExplore = { clicked = true },
                    onNavigateToBook = { _, _ -> }
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Tambah Buku")
            .performClick()

        assertTrue(clicked)
    }
}