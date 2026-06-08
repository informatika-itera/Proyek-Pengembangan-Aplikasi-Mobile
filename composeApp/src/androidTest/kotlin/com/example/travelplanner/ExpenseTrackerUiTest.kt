package com.example.travelplanner

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.travelplanner.core.util.LocalStrings
import com.example.travelplanner.core.util.StringsID
import com.example.travelplanner.domain.model.Expense
import com.example.travelplanner.presentation.screens.expenses.ExpenseSummaryCard
import org.junit.Rule
import org.junit.Test
import androidx.compose.runtime.CompositionLocalProvider

class ExpenseTrackerUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testExpenseSummaryCardShowsCorrectAmounts() {
        val dummyExpenses = listOf(
            Expense(
                id = "1",
                tripId = "trip_123",
                namaItem = "Kopi",
                nominal = 50000.0,
                kategori = "Konsumsi",
                createdAt = 123456789L
            ),
            Expense(
                id = "2",
                tripId = "trip_123",
                namaItem = "Gasing",
                nominal = 150000.0,
                kategori = "Wisata",
                createdAt = 123456789L
            )
        )

        composeTestRule.setContent {
            CompositionLocalProvider(LocalStrings provides StringsID) {
                ExpenseSummaryCard(totalExpenses = 200000.0, expenses = dummyExpenses)
            }
        }

        // Verify total sum formatted is displayed
        composeTestRule.onNodeWithText("Rp 200.000").assertExists()
        
        // Verify categories proportion percentages are rendered
        // Wisata is 150k out of 200k = 75%
        // Konsumsi is 50k out of 200k = 25%
        composeTestRule.onNodeWithText("75%").assertExists()
        composeTestRule.onNodeWithText("25%").assertExists()
    }
}
