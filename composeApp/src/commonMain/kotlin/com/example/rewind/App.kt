package com.example.rewind

import androidx.compose.runtime.Composable
import com.example.rewind.presentation.navigation.AppNavHost
import com.example.rewind.presentation.theme.RewindTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        RewindTheme {
            AppNavHost()
        }
    }
}
