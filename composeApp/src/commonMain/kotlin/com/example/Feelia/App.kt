package com.example.Feelia

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.example.Feelia.data.local.datastore.ThemeMode
import com.example.Feelia.data.local.datastore.UserPreferences
import com.example.Feelia.presentation.navigation.AppNavHost
import com.example.Feelia.presentation.theme.FeeliaTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject

@Composable
fun App() {
    val userPreferences: UserPreferences = koinInject()
    val themeMode by userPreferences.themeMode.collectAsStateWithLifecycle(ThemeMode.SYSTEM)

    FeeliaTheme(themeMode = themeMode) {
        AppNavHost()
    }
}