package com.example.foodsaver

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.foodsaver.data.local.datastore.ThemeMode
import com.example.foodsaver.data.local.datastore.UserPreferences
import com.example.foodsaver.presentation.navigation.AppNavHost
import com.example.foodsaver.presentation.theme.FoodSaverTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinContext {
        val userPreferences: UserPreferences = koinInject()
        val themeMode by userPreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
        
        val darkTheme = when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }

        FoodSaverTheme(darkTheme = darkTheme) {
            AppNavHost()
        }
    }
}
