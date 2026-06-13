package com.example.noteai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.noteai.data.local.datastore.UserPreferences
import com.example.noteai.presentation.navigation.AppNavHost
import com.example.noteai.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinContext {
        val userPreferences: UserPreferences = koinInject()
        val isDarkMode by userPreferences.isDarkMode.collectAsState(initial = false)

        NoteAITheme(darkTheme = isDarkMode) {
            AppNavHost()
        }
    }
}
