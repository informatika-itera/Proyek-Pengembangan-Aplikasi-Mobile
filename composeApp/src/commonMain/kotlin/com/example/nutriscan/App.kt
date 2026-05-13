package com.example.nutriscan

import androidx.compose.runtime.Composable
import com.example.nutriscan.presentation.navigation.AppNavHost
import com.example.nutriscan.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            AppNavHost()
        }
    }
}
