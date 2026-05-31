package com.example.pantaujompo

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.example.pantaujompo.presentation.navigation.AppNavHost
import com.example.pantaujompo.presentation.theme.PantauJompoTheme
import org.koin.compose.KoinContext
import java.util.Locale

/**
 * Composable utama aplikasi.
 * Mengatur tema, skala teks, dan bahasa secara global.
 */
@Composable
fun App() {
    KoinContext {
        // Ambil preferensi pengguna dari DataStore
        val userPreferences: com.example.pantaujompo.data.local.datastore.UserPreferences =
            org.koin.compose.koinInject()
        val isDarkMode by userPreferences.isDarkMode.collectAsState(initial = true)
        val textScale by userPreferences.textSizeScale.collectAsState(initial = 1.0f)
        val language by userPreferences.language.collectAsState(initial = "id")

        // Buat konfigurasi bahasa berdasarkan pilihan pengguna
        val locale = remember(language) {
            if (language == "en") Locale.ENGLISH else Locale("id", "ID")
        }
        val currentConfig = LocalConfiguration.current
        // Override konfigurasi dengan locale yang dipilih
        val updatedConfig = remember(locale) {
            Configuration(currentConfig).apply {
                setLocale(locale)
            }
        }

        PantauJompoTheme(darkTheme = isDarkMode) {
            val currentDensity = LocalDensity.current
            CompositionLocalProvider(
                // Terapkan skala teks global dari pengaturan
                LocalDensity provides Density(
                    density = currentDensity.density,
                    fontScale = currentDensity.fontScale * textScale
                ),
                // Terapkan bahasa global ke seluruh aplikasi
                LocalConfiguration provides updatedConfig
            ) {
                AppNavHost()
            }
        }
    }
}