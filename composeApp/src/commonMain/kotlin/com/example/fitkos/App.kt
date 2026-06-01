package com.example.fitkos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitkos.data.local.datastore.UserPreferences
import com.example.fitkos.presentation.navigation.AppNavHost
import com.example.fitkos.presentation.theme.FitKosTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinContext {
        val userPreferences: UserPreferences = koinInject()
        val isDarkMode by userPreferences.isDarkMode.collectAsStateWithLifecycle(initialValue = false)
        
        FitKosTheme(darkTheme = isDarkMode) {
            AppNavHost()
        }
    }
}
