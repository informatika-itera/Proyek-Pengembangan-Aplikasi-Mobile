package com.example.Feelia.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.Feelia.presentation.screens.settings.SettingsScreen
import com.example.Feelia.presentation.theme.FeeliaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ==================== LAYOUT ====================

    @Test
    fun settingsScreen_showsTitle() {
        composeTestRule.setContent {
            FeeliaTheme {
                SettingsScreen(onNavigateBack = {})
            }
        }

        composeTestRule
            .onNodeWithText("Pengaturan")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_showsThemeSection() {
        composeTestRule.setContent {
            FeeliaTheme {
                SettingsScreen(onNavigateBack = {})
            }
        }

        composeTestRule
            .onNodeWithText("Tampilan")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Tema")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_showsThemeOptions() {
        composeTestRule.setContent {
            FeeliaTheme {
                SettingsScreen(onNavigateBack = {})
            }
        }

        composeTestRule.onNodeWithText("Terang").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gelap").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ikuti Sistem").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_showsNotificationSection() {
        composeTestRule.setContent {
            FeeliaTheme {
                SettingsScreen(onNavigateBack = {})
            }
        }

        composeTestRule
            .onNodeWithText("Notifikasi")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Pengingat Jurnal")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_showsAboutSection() {
        composeTestRule.setContent {
            FeeliaTheme {
                SettingsScreen(onNavigateBack = {})
            }
        }

        composeTestRule
            .onNodeWithText("Tentang")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Feelia 🌸")
            .assertIsDisplayed()
    }

    // ==================== NAVIGATION ====================

    @Test
    fun backButton_onClick_navigatesBack() {
        var navigatedBack = false

        composeTestRule.setContent {
            FeeliaTheme {
                SettingsScreen(onNavigateBack = { navigatedBack = true })
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Kembali")
            .performClick()

        assertTrue(navigatedBack)
    }
}