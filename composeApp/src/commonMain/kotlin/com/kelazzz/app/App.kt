package com.kelazzz.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kelazzz.app.data.local.datastore.UserPreferences
import com.kelazzz.app.domain.model.ThemeMode
import com.kelazzz.app.presentation.navigation.AppNavHost
import com.kelazzz.app.presentation.theme.KelazZzTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinContext {
        val preferences: UserPreferences = koinInject()
        val themeMode by preferences.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
        val systemDarkTheme = isSystemInDarkTheme()
        val useDarkTheme = when (themeMode) {
            ThemeMode.SYSTEM -> systemDarkTheme
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        }

        KelazZzTheme(darkTheme = useDarkTheme) {
            AppNavHost()
        }
    }
}
