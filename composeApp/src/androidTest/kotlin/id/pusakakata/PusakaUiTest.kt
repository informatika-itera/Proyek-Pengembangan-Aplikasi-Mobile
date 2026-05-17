package id.pusakakata

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import id.pusakakata.ui.navigation.PusakaNavHost
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class PusakaUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_isDisplayed() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            PusakaNavHost(navController = navController)
        }

        composeTestRule.onNodeWithText("Pusaka Kata").assertExists()
    }

    @Test
    fun navigateToAbout_isDisplayed() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            PusakaNavHost(navController = navController)
        }

        composeTestRule.onNodeWithText("Info").performClick()
        composeTestRule.onNodeWithText("Tentang PusakaKata").assertExists()
    }

    @Test
    fun navigateToGacha_isDisplayed() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            PusakaNavHost(navController = navController)
        }

        composeTestRule.onNodeWithText("Gacha").performClick()
        composeTestRule.onNodeWithText("Pusaka Gacha").assertExists()
    }
}
