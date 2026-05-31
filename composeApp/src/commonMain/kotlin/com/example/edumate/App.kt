package com.example.edumate

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.edumate.data.local.datastore.ThemeMode
import com.example.edumate.data.local.datastore.UserPreferences
import com.example.edumate.presentation.navigation.AppNavHost
import com.example.edumate.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinContext {
        // Membaca DataStore secara langsung untuk mengetahui mode tema
        val userPreferences = koinInject<UserPreferences>()
        val themeMode by userPreferences.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)

        // Menentukan apakah menggunakan Dark Mode berdasarkan pilihan DataStore atau Sistem
        val darkTheme = when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }

        // Menerapkan tema yang dipilih ke seluruh aplikasi
        NoteAITheme(darkTheme = darkTheme) {
            val navController = rememberNavController()
            AppNavHost(navController = navController)
        }
    }
}