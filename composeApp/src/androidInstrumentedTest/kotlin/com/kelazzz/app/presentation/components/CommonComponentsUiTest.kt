package com.kelazzz.app.presentation.components

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.kelazzz.app.presentation.theme.KelazZzTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CommonComponentsUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyStateShowsTitleAndMessage() {
        composeRule.setContent {
            KelazZzTheme {
                EmptyState(
                    title = "Jadwal Kosong",
                    message = "Tambahkan agenda akademik pertama Anda."
                )
            }
        }

        composeRule.onNodeWithText("Jadwal Kosong").assertIsDisplayed()
        composeRule.onNodeWithText("Tambahkan agenda akademik pertama Anda.").assertIsDisplayed()
    }

    @Test
    fun errorStateShowsRetryButtonWhenCallbackProvided() {
        var retryClicked = false
        composeRule.setContent {
            KelazZzTheme {
                ErrorState(
                    message = "Gagal memuat data",
                    onRetry = { retryClicked = true }
                )
            }
        }

        composeRule.onNodeWithText("Oops!").assertIsDisplayed()
        composeRule.onNodeWithText("Gagal memuat data").assertIsDisplayed()
        composeRule.onNodeWithText("Coba Lagi").performClick()
        assertTrue(retryClicked)
    }

    @Test
    fun errorStateHidesRetryButtonWhenCallbackMissing() {
        composeRule.setContent {
            KelazZzTheme {
                ErrorState(message = "Mode offline aktif")
            }
        }

        composeRule.onNodeWithText("Mode offline aktif").assertIsDisplayed()
        composeRule.onAllNodesWithText("Coba Lagi").assertCountEquals(0)
    }
}
