package com.example.moneyz

import androidx.compose.runtime.Composable
import com.example.moneyz.presentation.navigation.AppNavHost
import com.example.moneyz.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            AppNavHost()
        }
    }
}
