package com.example.musickeep

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.musickeep.data.local.datastore.UserPreferences
import com.example.musickeep.presentation.navigation.AppNavHost
import com.example.musickeep.presentation.theme.MusicKeepTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinContext {
        val userPreferences: UserPreferences = koinInject()
        val isDarkMode by userPreferences.isDarkMode.collectAsState(initial = true)

        MusicKeepTheme(darkTheme = isDarkMode) {
            AppNavHost()
        }
    }
}
