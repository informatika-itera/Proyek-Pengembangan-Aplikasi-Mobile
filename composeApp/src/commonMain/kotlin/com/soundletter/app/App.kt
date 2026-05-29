package com.soundletter.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.soundletter.app.presentation.navigation.AppNavHost
import com.soundletter.app.presentation.screens.settings.SettingsViewModel
import com.soundletter.app.presentation.theme.SoundLetterTheme
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinContext {
        // Mengambil SettingsViewModel di level root untuk kontrol tema global
        val settingsViewModel: SettingsViewModel = koinViewModel()
        val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

        SoundLetterTheme(darkTheme = isDarkMode) {
            AppNavHost()
        }
    }
}
