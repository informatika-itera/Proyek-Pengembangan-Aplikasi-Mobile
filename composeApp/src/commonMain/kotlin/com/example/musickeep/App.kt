package com.example.musickeep

import androidx.compose.runtime.Composable
import com.example.musickeep.presentation.navigation.AppNavHost
import com.example.musickeep.presentation.theme.MusicKeepTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        MusicKeepTheme {
            AppNavHost()
        }
    }
}
