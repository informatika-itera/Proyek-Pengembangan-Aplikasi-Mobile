package com.example.rewind

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.rewind.data.local.datastore.UserPreferences
import com.example.rewind.presentation.navigation.AppNavHost
import com.example.rewind.presentation.theme.RewindTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinContext {
        val userPreferences: UserPreferences = koinInject()
        val isDarkMode by userPreferences.isDarkMode.collectAsState(initial = true) // default dark

        RewindTheme(darkTheme = isDarkMode) {
            AppNavHost()
        }
    }
}