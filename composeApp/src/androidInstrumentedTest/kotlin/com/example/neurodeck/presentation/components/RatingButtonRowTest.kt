package com.example.neurodeck.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.neurodeck.domain.model.ReviewRating
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test untuk [RatingButtonRow] — komponen rating di Study Session.
 * Menguji critical flow: keempat tombol tampil, tap memicu callback rating
 * yang benar, dan state disabled mencegah interaksi.
 */
@RunWith(AndroidJUnit4::class)
class RatingButtonRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun menampilkanKeempatTombolRating() {
        composeTestRule.setContent {
            MaterialTheme {
                RatingButtonRow(onRate = {})
            }
        }

        composeTestRule.onNodeWithText("Lupa").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sulit").assertIsDisplayed()
        composeTestRule.onNodeWithText("Oke").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mudah").assertIsDisplayed()
    }

    @Test
    fun tapOkeMemicuRatingGOOD() {
        var rated: ReviewRating? = null
        composeTestRule.setContent {
            MaterialTheme {
                RatingButtonRow(onRate = { rated = it })
            }
        }

        composeTestRule.onNodeWithText("Oke").performClick()

        assertEquals(ReviewRating.GOOD, rated)
    }

    @Test
    fun tapLupaMemicuRatingAGAIN() {
        var rated: ReviewRating? = null
        composeTestRule.setContent {
            MaterialTheme {
                RatingButtonRow(onRate = { rated = it })
            }
        }

        composeTestRule.onNodeWithText("Lupa").performClick()

        assertEquals(ReviewRating.AGAIN, rated)
    }

    @Test
    fun saatDisabledTombolTidakAktif() {
        var rated: ReviewRating? = null
        composeTestRule.setContent {
            MaterialTheme {
                RatingButtonRow(onRate = { rated = it }, enabled = false)
            }
        }

        // Pill yang disabled mengekspos semantics "not enabled"
        composeTestRule.onNodeWithText("Oke").assertIsNotEnabled()
        assertNull("Callback tidak boleh terpanggil sebelum interaksi", rated)
    }
}
