package com.example.Feelia.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.Feelia.domain.model.Emotion
import com.example.Feelia.domain.model.Note
import com.example.Feelia.presentation.screens.home.HomeScreen
import com.example.Feelia.presentation.screens.home.HomeUiState
import com.example.Feelia.presentation.theme.FeeliaTheme
import kotlinx.datetime.Clock
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

// Tambahan
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ==================== EMPTY STATE ====================

    @Test
    fun emptyState_showsEmptyMessage() {
        composeTestRule.setContent {
            FeeliaTheme {
                HomeScreen(
                    onNavigateToAddNote = {},
                    onNavigateToDetail = {},
                    onNavigateToAI = {},
                    onNavigateToSettings = {},
                    onNavigateToAnalytics = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Belum Ada Jurnal")
            .assertIsDisplayed()
    }

    @Test
    fun emptyState_showsCallToAction() {
        composeTestRule.setContent {
            FeeliaTheme {
                HomeScreen(
                    onNavigateToAddNote = {},
                    onNavigateToDetail = {},
                    onNavigateToAI = {},
                    onNavigateToSettings = {},
                    onNavigateToAnalytics = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Tap + untuk mulai menulis jurnal harimu 🌸")
            .assertIsDisplayed()
    }

    // ==================== NAVIGATION ====================

    @Test
    fun fabButton_onClick_navigatesToAddNote() {
        var navigated = false

        composeTestRule.setContent {
            FeeliaTheme {
                HomeScreen(
                    onNavigateToAddNote = { navigated = true },
                    onNavigateToDetail = {},
                    onNavigateToAI = {},
                    onNavigateToSettings = {},
                    onNavigateToAnalytics = {}
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Tulis Jurnal")
            .performClick()

        assertTrue(navigated)
    }

    @Test
    fun settingsButton_onClick_navigatesToSettings() {
        var navigated = false

        composeTestRule.setContent {
            FeeliaTheme {
                HomeScreen(
                    onNavigateToAddNote = {},
                    onNavigateToDetail = {},
                    onNavigateToAI = {},
                    onNavigateToSettings = { navigated = true },
                    onNavigateToAnalytics = {}
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Pengaturan")
            .performClick()

        assertTrue(navigated)
    }

    @Test
    fun analyticsButton_onClick_navigatesToAnalytics() {
        var navigated = false

        composeTestRule.setContent {
            FeeliaTheme {
                HomeScreen(
                    onNavigateToAddNote = {},
                    onNavigateToDetail = {},
                    onNavigateToAI = {},
                    onNavigateToSettings = {},
                    onNavigateToAnalytics = { navigated = true }
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Analitik")
            .performClick()

        assertTrue(navigated)
    }

    // ==================== TOP APP BAR ====================

    @Test
    fun topAppBar_showsFeeliaTitle() {
        composeTestRule.setContent {
            FeeliaTheme {
                HomeScreen(
                    onNavigateToAddNote = {},
                    onNavigateToDetail = {},
                    onNavigateToAI = {},
                    onNavigateToSettings = {},
                    onNavigateToAnalytics = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Feelia 🌸")
            .assertIsDisplayed()
    }

    // ==================== SEARCH ====================

    @Test
    fun searchIcon_onClick_showsSearchField() {
        composeTestRule.setContent {
            FeeliaTheme {
                HomeScreen(
                    onNavigateToAddNote = {},
                    onNavigateToDetail = {},
                    onNavigateToAI = {},
                    onNavigateToSettings = {},
                    onNavigateToAnalytics = {}
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Cari")
            .performClick()

        composeTestRule
            .onNodeWithText("Cari jurnal...")
            .assertIsDisplayed()
    }
}