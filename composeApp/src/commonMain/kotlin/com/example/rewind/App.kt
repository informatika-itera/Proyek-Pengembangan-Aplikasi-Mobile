package com.example.rewind

import androidx.compose.runtime.Composable
import com.example.rewind.presentation.navigation.AppNavHost
import com.example.rewind.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            AppNavHost()
        }
    }
}
