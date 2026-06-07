package com.soundletter.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class SoundLetterUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Skenario 1a: Flow mengirim pesan dari awal sampai tersimpan di history.
     */
    @Test
    fun testFlow_SendMessage_AndVerifyInHistory() {
        composeTestRule.setContent { App() }

        // 1. Tunggu Splash Screen selesai (Masuk Home)
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("SoundLetter").fetchSemanticsNodes().isNotEmpty()
        }

        // 2. Klik FAB untuk Compose
        composeTestRule.onNodeWithContentDescription("Compose").performClick()

        // 3. Isi Form
        composeTestRule.onNodeWithText("Untuk").performTextInput("Automation User")
        composeTestRule.onNodeWithText("Isi Pesan").performTextInput("Pesan testing Sprint 4")
        
        // 4. Kirim
        composeTestRule.onNodeWithText("Kirim Surat Musik").performClick()

        // 5. Tunggu kembali ke Home dan cek History via Bottom Bar
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithContentDescription("history").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithContentDescription("history").performClick()

        // 6. Verifikasi data muncul
        composeTestRule.onNodeWithText("To: Automation User", substring = true).assertIsDisplayed()
    }

    /**
     * Skenario 1b: Flow mencari (search) surat dan verifikasi hasil muncul di list.
     */
    @Test
    fun testFlow_SearchLetter_VerifyResult() {
        composeTestRule.setContent { App() }

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithContentDescription("search").fetchSemanticsNodes().isNotEmpty()
        }

        // 1. Navigasi ke Search
        composeTestRule.onNodeWithContentDescription("search").performClick()

        // 2. Ketik nama penerima (Gunakan data dummy 'Atalie')
        composeTestRule.onNodeWithText("Ketik nama penerima...").performTextInput("Atalie")

        // 3. Verifikasi hasil muncul
        composeTestRule.onNodeWithText("To: Atalie Salsabila", substring = true).assertIsDisplayed()
    }

    /**
     * Skenario 1c: Flow navigasi Home -> Settings -> Home (Tanpa Dead End).
     */
    @Test
    fun testFlow_Navigation_HomeToSettingsAndBack() {
        composeTestRule.setContent { App() }

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithContentDescription("settings").fetchSemanticsNodes().isNotEmpty()
        }

        // 1. Ke Settings
        composeTestRule.onNodeWithContentDescription("settings").performClick()
        composeTestRule.onNodeWithText("Pengaturan").assertIsDisplayed()

        // 2. Klik Back
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // 3. Verifikasi kembali ke Home
        composeTestRule.onNodeWithText("SoundLetter").assertIsDisplayed()
    }
}
