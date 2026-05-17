package com.example.pocketguard

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pocketguard.presentation.navigation.AppNavHost
import com.example.pocketguard.presentation.screens.settings.SettingsViewModel
import com.example.pocketguard.presentation.theme.PocketGuardTheme
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.getValue

@Composable
fun App(viewModel: SettingsViewModel = koinViewModel ()) {
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    KoinContext {
        PocketGuardTheme(darkTheme = isDarkMode) {
            AppNavHost()
        }
    }
}
