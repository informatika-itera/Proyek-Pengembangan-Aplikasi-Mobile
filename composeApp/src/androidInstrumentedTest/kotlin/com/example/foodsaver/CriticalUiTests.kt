package com.example.foodsaver

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class CriticalUiTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAddFoodFlow() {
        composeTestRule.onNodeWithTag("fab_add_food").performClick()

        composeTestRule.onNodeWithTag("tf_food_name").performTextInput("Susu Ultra")
        composeTestRule.onNodeWithTag("tf_food_quantity").performTextReplacement("2")
        
        composeTestRule.onNodeWithTag("btn_save_food").performClick()

        composeTestRule.onNodeWithText("Susu Ultra").assertIsDisplayed()
    }

    @Test
    fun testRecipeManualInputFlow() {
        composeTestRule.onNodeWithTag("nav_recipe").performClick()

        composeTestRule.onNodeWithTag("tab_manual").performClick()

        composeTestRule.onNodeWithTag("tf_manual_ingredient").performTextInput("Telur, Nasi")
        composeTestRule.onNodeWithTag("btn_add_manual").performClick()

        composeTestRule.onNodeWithTag("btn_generate_recipe").performClick()

        composeTestRule.onNodeWithTag("txt_recipe_title").assertIsDisplayed()
    }

    @Test
    fun testDetailAndDeleteFlow() {
        if (composeTestRule.onAllNodesWithTag("home_food_list").fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onAllNodes(hasClickAction()).onFirst().performClick()

            composeTestRule.onNodeWithTag("food_detail_content").assertIsDisplayed()

            composeTestRule.onNodeWithTag("btn_delete_food").performClick()
            
            composeTestRule.onNodeWithTag("btn_confirm_delete").performClick()

            composeTestRule.onNodeWithTag("fab_add_food").assertIsDisplayed()
        }
    }
}
