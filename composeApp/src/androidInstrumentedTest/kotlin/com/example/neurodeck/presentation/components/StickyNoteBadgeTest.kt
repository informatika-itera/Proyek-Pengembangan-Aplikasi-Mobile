package com.example.neurodeck.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test untuk [StickyNoteBadge] — chip kecil reusable.
 * Memverifikasi teks yang dilewatkan benar-benar dirender.
 */
@RunWith(AndroidJUnit4::class)
class StickyNoteBadgeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun menampilkanTeksYangDiberikan() {
        composeTestRule.setContent {
            MaterialTheme {
                StickyNoteBadge(text = "PERTANYAAN")
            }
        }

        composeTestRule.onNodeWithText("PERTANYAAN").assertIsDisplayed()
    }

    @Test
    fun menampilkanTeksBerbeda() {
        composeTestRule.setContent {
            MaterialTheme {
                StickyNoteBadge(text = "JAWABAN")
            }
        }

        composeTestRule.onNodeWithText("JAWABAN").assertIsDisplayed()
    }
}
