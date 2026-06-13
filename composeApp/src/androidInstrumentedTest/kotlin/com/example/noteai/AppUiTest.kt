package com.example.noteai

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testLoginFlow() {
        composeTestRule.onNodeWithText("Selamat Datang!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").performTextInput("test@mail.com")
        composeTestRule.onNodeWithText("Password").performTextInput("123456")
        composeTestRule.onNodeWithText("Masuk").performClick()
    }

    @Test
    fun testNavigationBetweenTabs() {
        composeTestRule.onNodeWithContentDescription("Pantry").performClick()
        composeTestRule.onNodeWithText("Inventory Dapur").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Recipes").performClick()
        composeTestRule.onNodeWithText("Buku Resep").assertIsDisplayed()
    }

    @Test
    fun testAddPantryItemFlow() {
        composeTestRule.onNodeWithContentDescription("Pantry").performClick()
        composeTestRule.onNodeWithContentDescription("Tambah Bahan").performClick()
        composeTestRule.onNodeWithText("Nama Bahan (cth: Beras)").performTextInput("Garam")
        composeTestRule.onNodeWithText("Simpan Stok").performClick()
        composeTestRule.onNodeWithText("Garam").assertIsDisplayed()
    }

    @Test
    fun testRecipeClickNavigation() {
        composeTestRule.onNodeWithContentDescription("Recipes").performClick()
        // Assuming there is at least one recipe or we add one
        composeTestRule.onNodeWithText("Buku Resep").assertIsDisplayed()
    }

    @Test
    fun testProfileDisplay() {
        composeTestRule.onNodeWithContentDescription("Profile").performClick()
        composeTestRule.onNodeWithText("Profil Pengguna").assertIsDisplayed()
        composeTestRule.onNodeWithText("Informasi Akun").assertIsDisplayed()
    }
}
