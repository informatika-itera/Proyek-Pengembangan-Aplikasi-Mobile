package com.example.raillog

import androidx.compose.runtime.Composable
import com.example.raillog.presentation.navigation.AppNavHost
import com.example.raillog.presentation.theme.RailLogTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        RailLogTheme {
            AppNavHost()
        }
    }
}