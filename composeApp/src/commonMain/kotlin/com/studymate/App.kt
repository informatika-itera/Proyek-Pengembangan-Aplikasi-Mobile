package com.studymate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import com.studymate.presentation.navigation.AppNavHost
import com.studymate.presentation.theme.StudyMateTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    var isDarkTheme by remember { mutableStateOf(false) } // Default to light mode

    KoinContext {
        StudyMateTheme(darkTheme = isDarkTheme) {
            AppNavHost(
                isDarkTheme = isDarkTheme,
                onThemeToggle = { isDarkTheme = !isDarkTheme }
            )
        }
    }
}
