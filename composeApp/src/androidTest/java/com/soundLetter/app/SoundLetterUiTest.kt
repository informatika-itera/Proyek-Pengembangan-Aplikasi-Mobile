package com.soundletter.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.soundletter.app.presentation.screens.compose.ComposeScreen
import com.soundletter.app.presentation.screens.history.HistoryScreen
import com.soundletter.app.presentation.theme.SoundLetterTheme
import org.junit.Rule
import org.junit.Test
import org.koin.compose.KoinContext

class SoundLetterUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Test 1 (Input Interaction):
     * Memastikan teks yang diketik di TextField "To" muncul di UI.
     */
    @Test
    fun testRecipientInput_ShouldDisplayTypedText() {
        composeTestRule.setContent {
            SoundLetterTheme {
                KoinContext {
                    ComposeScreen(onNavigateBack = {})
                }
            }
        }

        // Mencari TextField dengan teks label "To"
        composeTestRule.onNodeWithText("To")
            .performTextInput("Dzaky")

        // Memastikan teks "Dzaky" kini ada di dalam TextField tersebut
        composeTestRule.onNodeWithText("Dzaky")
            .assertIsDisplayed()
    }

    /**
     * Test 2 (Action & Feedback Validation):
     * Memastikan pesan error muncul jika tombol kirim ditekan saat input kosong.
     */
    @Test
    fun testSendButton_EmptyFields_ShowsError() {
        composeTestRule.setContent {
            SoundLetterTheme {
                KoinContext {
                    ComposeScreen(onNavigateBack = {})
                }
            }
        }

        // Klik tombol "Send Letter" tanpa mengisi TextField
        composeTestRule.onNodeWithText("Send Letter")
            .performClick()

        // Menunggu hingga Snackbar muncul. 
        // Kita mencari teks spesifik yang dilempar oleh ComposeViewModel.
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("cannot be empty", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Memastikan indikator error ditampilkan di layar
        composeTestRule.onNodeWithText("cannot be empty", substring = true)
            .assertIsDisplayed()
    }

    /**
     * Test 3 (List Rendering):
     * Memastikan HistoryScreen menampilkan daftar kartu pesan.
     */
    @Test
    fun testHistoryScreen_RendersMessageCards() {
        composeTestRule.setContent {
            SoundLetterTheme {
                KoinContext {
                    HistoryScreen(
                        onNavigateBack = {},
                        onNavigateToDetail = {}
                    )
                }
            }
        }

        // Memastikan kartu pesan dengan awalan "To:" (dari data dummy) tampil
        composeTestRule.onAllNodesWithText("To:", substring = true)
            .onFirst()
            .assertIsDisplayed()
    }
}
