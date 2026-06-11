package id.pusakakata

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import id.pusakakata.presentation.navigation.AppNavHost
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test
import id.pusakakata.presentation.theme.PusakaKataTheme

class PusakaUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_isDisplayed() {
        composeTestRule.setContent {
            PusakaKataTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }

        composeTestRule.onNodeWithText("Pusaka Kata").assertExists()
    }

    @Test
    fun navigateToFavorite_isDisplayed() {
        composeTestRule.setContent {
            PusakaKataTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }

        composeTestRule.onNodeWithText("Favorit").performClick()
        composeTestRule.onNodeWithText("Pusaka Favorit ❤️").assertExists()
    }

    @Test
    fun navigateToProfile_isDisplayed() {
        composeTestRule.setContent {
            PusakaKataTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }

        composeTestRule.onNodeWithText("Profil").performClick()
        composeTestRule.onNodeWithText("Profil Pengembara").assertExists()
    }
}
