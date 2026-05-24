package com.studyhub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studyhub.presentation.navigation.AppNavHost
import com.studyhub.presentation.theme.StudyHubTheme
import com.studyhub.presentation.theme.ThemeViewModel
import com.studyhub.core.util.SystemAppearance
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinContext {
        val themeViewModel: ThemeViewModel = koinViewModel()
        val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
        
        SystemAppearance(isDarkMode = isDarkMode)
        
        StudyHubTheme(darkTheme = isDarkMode) {
            AppNavHost()
        }
    }
}
