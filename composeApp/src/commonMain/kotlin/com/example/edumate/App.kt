package com.example.edumate

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

            // SOLUSI BLANK SCREEN & FLASH PUTIH:
            // Membungkus seluruh navigasi dengan Surface yang memiliki warna background bawaan tema
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }

        }
    }
}