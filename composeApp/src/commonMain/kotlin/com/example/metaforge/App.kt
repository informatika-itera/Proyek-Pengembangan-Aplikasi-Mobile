package com.example.metaforge

import androidx.compose.runtime.Composable
import com.example.metaforge.presentation.navigation.AppNavHost
import com.example.metaforge.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            AppNavHost()
        }
    }
}
