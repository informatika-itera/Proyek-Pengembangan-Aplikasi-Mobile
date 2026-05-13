package com.example.fitgen

import androidx.compose.runtime.Composable
import com.example.fitgen.presentation.navigation.AppNavHost
import com.example.fitgen.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            AppNavHost()
        }
    }
}
