package com.studymate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import com.studymate.presentation.AppViewModel
import com.studymate.presentation.navigation.AppNavHost
import com.studymate.presentation.theme.StudyMateTheme
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinContext {
        val appViewModel: AppViewModel = koinViewModel()
        val isDarkTheme by appViewModel.isDarkTheme.collectAsState()

        StudyMateTheme(darkTheme = isDarkTheme) {
            AppNavHost(
                isDarkTheme = isDarkTheme,
                onThemeToggle = { appViewModel.toggleTheme() }
            )
        }
    }
}
