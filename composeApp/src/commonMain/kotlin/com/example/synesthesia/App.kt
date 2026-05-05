package com.example.synesthesia

import androidx.compose.runtime.Composable
import com.example.synesthesia.presentation.navigation.AppNavHost
import com.example.synesthesia.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            AppNavHost()
        }
    }
}
