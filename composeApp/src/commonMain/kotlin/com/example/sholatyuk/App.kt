package com.example.sholatyuk

import androidx.compose.runtime.Composable
import com.example.sholatyuk.presentation.navigation.AppNavHost
import com.example.sholatyuk.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            AppNavHost()
        }
    }
}


