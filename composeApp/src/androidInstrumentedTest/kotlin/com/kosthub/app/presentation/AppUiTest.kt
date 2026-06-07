package com.kosthub.app.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.presentation.screens.home.HomeScreen
import com.kosthub.app.presentation.state.UiState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@RunWith(AndroidJUnit4::class)
class AppUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleKosts = listOf(
        Kost(1L, "Kost Iterasi", "0812", 0.5, 5000000, "Campur", "Dalam", "Ada", "Ada", "Ada", "Ada", "Tidak", "Ada", "Ada", "Ada", false),
        Kost(2L, "Kost Mawar", "0813", 1.2, 6000000, "Perempuan", "Luar", "Ada", "Ada", "Ada", "Ada", "AC", "Ada", "Ada", "Ada", false)
    )

    @Test
    fun testSearchQueryChanges() {
        composeTestRule.setContent {
            var query by remember { mutableStateOf("") }
            HomeScreen(
                uiState = UiState.Success(sampleKosts),
                searchQuery = query,
                onQueryChange = { query = it },
                selectedTipeKos = null,
                onTipeKosChange = {},
                onNavigateDetail = {},
                onToggleFavorite = {}
            )
        }

        // Search text field can be found by text or placeholder
        composeTestRule.onNodeWithText("Cari lokasi atau nama kost...").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cari lokasi atau nama kost...").performTextInput("Mawar")
    }

    @Test
    fun testFilterChipClick() {
        var selectedTipe: String? = null
        composeTestRule.setContent {
            HomeScreen(
                uiState = UiState.Success(sampleKosts),
                searchQuery = "",
                onQueryChange = {},
                selectedTipeKos = selectedTipe,
                onTipeKosChange = { selectedTipe = it },
                onNavigateDetail = {},
                onToggleFavorite = {}
            )
        }

        composeTestRule.onNodeWithText("Perempuan").performClick()
    }

    @Test
    fun testNavigateToDetailOnClick() {
        var clickedId: Long? = null
        composeTestRule.setContent {
            HomeScreen(
                uiState = UiState.Success(sampleKosts),
                searchQuery = "",
                onQueryChange = {},
                selectedTipeKos = null,
                onTipeKosChange = {},
                onNavigateDetail = { clickedId = it },
                onToggleFavorite = {}
            )
        }

        // Click on the Kost card
        composeTestRule.onNodeWithText("Kost Iterasi").performClick()
    }
}
