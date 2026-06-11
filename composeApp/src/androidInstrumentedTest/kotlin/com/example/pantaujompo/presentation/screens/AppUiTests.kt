package com.example.pantaujompo.presentation.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import com.example.pantaujompo.presentation.screens.profile.ProfileSetupScreen
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppUiTests {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testProfileSetupScreen_initialState() = runComposeUiTest {
        setContent {
            ProfileSetupScreen(onSaveClick = { _, _, _, _, _ -> })
        }
        // Wait for animation
        mainClock.advanceTimeBy(1000L)
        onNodeWithText("Laki-laki").assertExists()
        onNodeWithText("Selamat Datang di\nPantau Jompo.").assertExists()
    }
}
