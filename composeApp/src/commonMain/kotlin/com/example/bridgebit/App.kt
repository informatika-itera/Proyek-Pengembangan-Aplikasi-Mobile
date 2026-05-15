package com.example.bridgebit

import androidx.compose.runtime.Composable
import com.example.bridgebit.presentation.navigation.AppNavHost
import com.example.bridgebit.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            AppNavHost()
        }
    }
}
