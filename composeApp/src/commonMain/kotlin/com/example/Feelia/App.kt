package com.example.Feelia

import androidx.compose.runtime.Composable
import com.example.Feelia.presentation.navigation.AppNavHost
import com.example.Feelia.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            AppNavHost()
        }
    }
}
