package com.example.studyplanner

import androidx.compose.runtime.Composable
import com.example.studyplanner.presentation.navigation.AppNavHost
import com.example.studyplanner.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            AppNavHost()
        }
    }
}
