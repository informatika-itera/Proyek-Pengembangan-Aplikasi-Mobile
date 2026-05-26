package com.example.hujjah

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.hujjah.data.local.datastore.UserPreferences
import com.example.hujjah.presentation.navigation.AppNavHost
import org.koin.compose.koinInject

@Composable
fun App() {
    val userPreferences = koinInject<UserPreferences>()
    val isDarkMode by userPreferences.isDarkMode.collectAsState(initial = false)

    com.example.hujjah.presentation.theme.HujjahTheme(darkTheme = isDarkMode) {
        AppNavHost()
    }
}