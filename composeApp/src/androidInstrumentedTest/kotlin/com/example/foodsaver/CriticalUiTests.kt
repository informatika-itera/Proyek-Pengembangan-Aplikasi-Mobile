package com.example.foodsaver

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

/**
 * UI Tests untuk Sprint 4 - Critical User Journeys.
 * Pastikan aplikasi berjalan di emulator/device sebelum menjalankan test ini.
 */
class CriticalUiTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAddFoodFlow() {
        // 1. Klik FAB Tambah Makanan
        composeTestRule.onNodeWithTag("fab_add_food").performClick()

        // 2. Isi Form
        composeTestRule.onNodeWithTag("tf_food_name").performTextInput("Susu Ultra")
        composeTestRule.onNodeWithTag("tf_food_quantity").performTextReplacement("2")
        
        // 3. Simpan
        composeTestRule.onNodeWithTag("btn_save_food").performClick()

        // 4. Pastikan kembali ke Home dan makanan muncul
        composeTestRule.onNodeWithText("Susu Ultra").assertIsDisplayed()
    }

    @Test
    fun testRecipeManualInputFlow() {
        // 1. Pindah ke Tab Resep
        composeTestRule.onNodeWithTag("nav_recipe").performClick()

        // 2. Pilih Mode Manual
        composeTestRule.onNodeWithTag("tab_manual").performClick()

        // 3. Input Bahan Manual
        composeTestRule.onNodeWithTag("tf_manual_ingredient").performTextInput("Telur, Nasi")
        composeTestRule.onNodeWithTag("btn_add_manual").performClick()

        // 4. Buat Rekomendasi
        composeTestRule.onNodeWithTag("btn_generate_recipe").performClick()

        // 5. Pastikan hasil resep muncul
        composeTestRule.onNodeWithTag("txt_recipe_title").assertIsDisplayed()
    }

    @Test
    fun testDetailAndDeleteFlow() {
        // 1. Klik salah satu item makanan di Home (asumsi ada data "Susu Ultra")
        // Jika list kosong, test ini akan skip atau gagal sesuai kondisi data
        if (composeTestRule.onAllNodesWithTag("home_food_list").fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onAllNodes(hasClickAction()).onFirst().performClick()

            // 2. Pastikan masuk ke Detail
            composeTestRule.onNodeWithTag("food_detail_content").assertIsDisplayed()

            // 3. Klik Hapus
            composeTestRule.onNodeWithTag("btn_delete_food").performClick()
            
            // 4. Konfirmasi Hapus di Dialog
            composeTestRule.onNodeWithTag("btn_confirm_delete").performClick()

            // 5. Pastikan kembali ke Home
            composeTestRule.onNodeWithTag("fab_add_food").assertIsDisplayed()
        }
    }
}
