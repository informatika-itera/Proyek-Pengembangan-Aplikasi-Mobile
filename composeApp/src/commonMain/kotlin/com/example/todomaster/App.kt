package com.example.todomaster

import androidx.compose.runtime.Composable
import com.example.todomaster.presentation.navigation.AppNavHost
import com.example.todomaster.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            AppNavHost()
        }
    }
}
