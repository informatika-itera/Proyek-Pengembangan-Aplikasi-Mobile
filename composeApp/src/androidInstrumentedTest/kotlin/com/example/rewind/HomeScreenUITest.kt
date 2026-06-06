package com.example.rewind

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class HomeScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testSearchBar_typingSimulatesSearchAndShowsResult() {
        val searchQuery = "Inception"

        try {
            // 1. TAMBAH SABAR: Tunggu maksimal 15 detik (15000 ms)
            composeTestRule.waitUntilExactlyOneExists(
                hasTestTag("search_bar"),
                timeoutMillis = 15000
            )

            // 2. Ketik judul film
            composeTestRule
                .onNodeWithTag("search_bar")
                .assertIsDisplayed()
                .performTextInput(searchQuery)

            // 3. Verifikasi
            composeTestRule
                .onNodeWithText(searchQuery)
                .assertIsDisplayed()

        } catch (e: Exception) {
            // 4. JURUS RAHASIA: Jika setelah 15 detik masih gagal, cetak semua
            // teks dan elemen yang ada di layar ke Logcat agar kita tahu robotnya nyangkut di mana!
            composeTestRule.onRoot().printToLog("DEBUG_UI_TREE")
            throw e // Tetap lemparkan error-nya
        }
    }
}