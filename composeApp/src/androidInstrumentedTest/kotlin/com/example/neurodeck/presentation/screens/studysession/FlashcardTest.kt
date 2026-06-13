package com.example.neurodeck.presentation.screens.studysession

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test untuk [Flashcard] — kartu utama di Study Session.
 * Critical flow: sisi depan menampilkan pertanyaan + badge "PERTANYAAN",
 * sisi belakang menampilkan jawaban + badge "JAWABAN", dan tap pada sisi
 * depan memicu callback onTap (untuk flip).
 */
@RunWith(AndroidJUnit4::class)
class FlashcardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun sisiDepanMenampilkanPertanyaan() {
        composeTestRule.setContent {
            MaterialTheme {
                Flashcard(
                    front = "Apa ibukota Indonesia?",
                    back = "Jakarta",
                    showingBack = false,
                    onTap = {},
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("PERTANYAAN").assertIsDisplayed()
        composeTestRule.onNodeWithText("Apa ibukota Indonesia?").assertIsDisplayed()
    }

    @Test
    fun sisiBelakangMenampilkanJawaban() {
        composeTestRule.setContent {
            MaterialTheme {
                Flashcard(
                    front = "Apa ibukota Indonesia?",
                    back = "Jakarta",
                    showingBack = true,
                    onTap = {},
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("JAWABAN").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jakarta").assertIsDisplayed()
    }

    @Test
    fun tapPadaSisiDepanMemicuOnTap() {
        var tapped = false
        composeTestRule.setContent {
            MaterialTheme {
                Flashcard(
                    front = "Pertanyaan",
                    back = "Jawaban",
                    showingBack = false,
                    onTap = { tapped = true },
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("PERTANYAAN").performClick()

        assertTrue("onTap harus terpanggil saat kartu di-tap", tapped)
    }
}
