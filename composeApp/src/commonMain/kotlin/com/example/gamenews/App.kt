package com.example.gamenews.presentation

import androidx.compose.runtime.Composable
import com.example.gamenews.presentation.navigation.AppNavHost
import com.example.gamenews.presentation.theme.GameNewsTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {

        GameNewsTheme {
            AppNavHost()
        }
    }
}