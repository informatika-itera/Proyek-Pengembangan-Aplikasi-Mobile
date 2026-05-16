package com.soundletter.app

import androidx.compose.runtime.Composable
import com.soundletter.app.presentation.navigation.AppNavHost
import com.soundletter.app.presentation.theme.SoundLetterTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        SoundLetterTheme {
            AppNavHost()
        }
    }
}
