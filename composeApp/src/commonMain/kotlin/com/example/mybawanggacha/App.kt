package com.example.mybawanggacha

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.mybawanggacha.domain.settings.model.ThemeMode
import com.example.mybawanggacha.domain.settings.repository.SettingsRepository
import com.example.mybawanggacha.presentation.navigation.AppNavHost
import com.example.mybawanggacha.presentation.theme.MBGTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App(
    onDarkThemeChange: (Boolean) -> Unit = {}
) {
    KoinContext {
        val settingsRepository = koinInject<SettingsRepository>()
        val systemDarkTheme = isSystemInDarkTheme()
        val themeMode by settingsRepository.themeMode.collectAsState(initial = ThemeMode.System)
        val isDarkMode = themeMode.resolve(systemDarkTheme)

        LaunchedEffect(isDarkMode) {
            onDarkThemeChange(isDarkMode)
        }

        MBGTheme(darkTheme = isDarkMode) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavHost(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
