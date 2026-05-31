package com.itera.news

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.compose.ui.test.hasSetTextAction
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsUITest {

    // Membuka MainActivity sebagai awal dari UI Test
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun verifikasiHeaderDanTampilanAwal() {
        // Memastikan teks judul aplikasi muncul di layar
        composeTestRule.onNodeWithText("NEWS MBG AI").assertExists()
        // Memastikan kategori default (Semua) muncul
        composeTestRule.onNodeWithText("Semua").assertExists()
    }

    @Test
    fun verifikasiInputPencarian() {
        // Mencari komponen yang berfungsi sebagai TextField, lalu mengetik teks
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Teknologi")
        // Memastikan teks yang diketik masuk ke dalam field
        composeTestRule.onNodeWithText("Teknologi").assertExists()
    }

    @Test
    fun verifikasiKlikKategori() {
        // Mengklik tab kategori "Pro"
        composeTestRule.onNodeWithText("Pro").performClick()
        composeTestRule.onNodeWithText("Pro").assertExists()
    }
}