package com.example.inventra.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventra.presentation.screens.auth.LoginScreen
import com.example.inventra.presentation.theme.InventRaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Critical Flow UI Test for Login.
 * Sprint 4 fix with scroll support and unmerged tree.
 */
@RunWith(AndroidJUnit4::class)
class CriticalFlowsUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_loginButtonIsDisplayed() {
        composeTestRule.setContent {
            InventRaTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }

        // Cari tombol login, scroll jika perlu
        composeTestRule.onNodeWithText("Masuk", useUnmergedTree = true)
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsErrorWhenFieldsAreEmpty() {
        composeTestRule.setContent {
            InventRaTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }

        // Tap login tanpa mengisi apapun
        composeTestRule.onNodeWithText("Masuk", useUnmergedTree = true)
            .performScrollTo()
            .performClick()

        // Error message dari LoginViewModel: "Email dan password harus diisi"
        composeTestRule.onNodeWithText("Email dan password harus diisi")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun loginScreen_canTypeEmail() {
        composeTestRule.setContent {
            InventRaTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }

        composeTestRule.onNodeWithText("Email")
            .performScrollTo()
            .performTextInput("admin@hmif.itera.ac.id")
            
        composeTestRule.onNodeWithText("Email")
            .assertTextContains("admin@hmif.itera.ac.id")
    }

    @Test
    fun loginScreen_passwordFieldIsDisplayed() {
        composeTestRule.setContent {
            InventRaTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }

        composeTestRule.onNodeWithText("Password")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsInventRaBranding() {
        composeTestRule.setContent {
            InventRaTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }

        composeTestRule.onNodeWithText("InventRa").assertIsDisplayed()
    }
}
