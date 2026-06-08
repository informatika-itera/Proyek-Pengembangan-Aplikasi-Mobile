package com.example.fitkos.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.fitkos.MainActivity
import org.junit.Rule
import org.junit.Test

class CriticalFlowTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testDashboardDisplayedOnStart() {
        // Tunggu Splash Screen selesai (jika ada) dan cek apakah Dashboard muncul
        composeTestRule.onNodeWithText("Dashboard", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Daily Summary", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun testNavigateToAddNoteAndInput() {
        // Klik tombol tambah (biasanya FAB)
        // Kita cari berdasarkan content description atau icon jika teks tidak ada
        // Mengasumsikan ada tombol dengan teks "Tambah" atau icon "+" 
        composeTestRule.onNodeWithContentDescription("Add Note", ignoreCase = true).performClick()

        // Cek apakah form muncul dan bisa diketik
        composeTestRule.onNodeWithText("Nama Makanan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nama Makanan").performTextInput("Nasi Goreng")
        
        composeTestRule.onNodeWithText("Nasi Goreng").assertExists()
    }

    @Test
    fun testWaterTrackerIncrement() {
        // Navigasi ke Water Tracker (melalui Drawer atau tombol)
        composeTestRule.onNodeWithContentDescription("Open Navigation Drawer").performClick()
        composeTestRule.onNodeWithText("Tracker Air").performClick()

        // Cek apakah layar Water Tracker muncul
        composeTestRule.onNodeWithText("Water Tracker", ignoreCase = true).assertIsDisplayed()
        
        // Klik tombol tambah gelas (asumsi ada tombol dengan "+")
        composeTestRule.onNodeWithText("+").performClick()
        
        // Verifikasi ada perubahan (misal angka 1 muncul)
        composeTestRule.onNodeWithText("1").assertIsDisplayed()
    }
}
