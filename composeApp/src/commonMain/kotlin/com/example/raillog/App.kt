package com.example.raillog

import androidx.compose.runtime.Composable
import com.example.raillog.presentation.navigation.AppNavHost
import com.example.raillog.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            AppNavHost()
        }
    }
}
