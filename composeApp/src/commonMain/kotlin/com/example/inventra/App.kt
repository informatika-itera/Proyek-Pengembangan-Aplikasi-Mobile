package com.example.inventra

import androidx.compose.runtime.Composable
import com.example.inventra.presentation.navigation.AppNavHost
import com.example.inventra.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            AppNavHost()
        }
    }
}
