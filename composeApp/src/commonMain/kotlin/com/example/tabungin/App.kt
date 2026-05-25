package com.example.tabungin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.tabungin.data.local.datastore.UserPreferences
import com.example.tabungin.presentation.navigation.AppNavHost
import com.example.tabungin.presentation.theme.TabungInTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinContext {
        val userPreferences: UserPreferences = koinInject()
        val isDarkMode by userPreferences.isDarkMode.collectAsState(initial = false)

        TabungInTheme(darkTheme = isDarkMode) {
            AppNavHost()
        }
    }
}