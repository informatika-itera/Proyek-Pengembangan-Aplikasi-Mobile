package com.example.tripmate.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.tripmate.domain.model.Trip
import com.example.tripmate.presentation.screens.home.TripCard
import com.example.tripmate.presentation.screens.home.HomeScreen
import com.example.tripmate.presentation.theme.TripMateTheme
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tripCard_showsDestinationAndBudget() {
        val trip = Trip(
            id = 1L,
            destination = "Bali",
            startDate = "2024-06-01",
            endDate = "2024-06-07",
            budget = 5000000.0,
            createdAt = System.currentTimeMillis()
        )

        composeTestRule.setContent {
            TripMateTheme {
                TripCard(trip = trip, onClick = {}, onDeleteClick = {})
            }
        }

        composeTestRule.onNodeWithText("Bali").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rp 5.000.000").assertIsDisplayed()
    }

    @Test
    fun tripCard_deleteClick_triggers() {
        var deleted = false
        val trip = Trip(
            id = 1L,
            destination = "Lombok",
            startDate = "2024-07-01",
            endDate = "2024-07-05",
            budget = 3000000.0,
            createdAt = System.currentTimeMillis()
        )

        composeTestRule.setContent {
            TripMateTheme {
                TripCard(trip = trip, onClick = {}, onDeleteClick = { deleted = true })
            }
        }

        composeTestRule.onNodeWithContentDescription("Hapus").performClick()
        assertTrue(deleted)
    }

    @Test
    fun tripCard_onClick_triggers() {
        var clicked = false
        val trip = Trip(
            id = 1L,
            destination = "Yogyakarta",
            startDate = "2024-08-01",
            endDate = "2024-08-03",
            budget = 2000000.0,
            createdAt = System.currentTimeMillis()
        )

        composeTestRule.setContent {
            TripMateTheme {
                TripCard(trip = trip, onClick = { clicked = true }, onDeleteClick = {})
            }
        }

        composeTestRule.onNodeWithText("Yogyakarta").performClick()
        assertTrue(clicked)
    }
}
