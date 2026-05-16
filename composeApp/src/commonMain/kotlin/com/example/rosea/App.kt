package com.example.rosea

import androidx.compose.runtime.Composable
import com.example.rosea.presentation.navigation.AppNavHost
import com.example.rosea.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            AppNavHost()
        }
    }
}
