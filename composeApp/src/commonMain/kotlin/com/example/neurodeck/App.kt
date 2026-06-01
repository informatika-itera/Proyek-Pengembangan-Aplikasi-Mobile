package com.example.neurodeck

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.neurodeck.domain.model.ThemeMode
import com.example.neurodeck.domain.repository.UserPreferencesRepository
import com.example.neurodeck.presentation.navigation.AppNavHost
import com.example.neurodeck.presentation.theme.neurodeckTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

/**
 * Entry point composable aplikasi NeuroDeck.
 *
 * Wrap urutan (dari luar ke dalam):
 *   1. KoinContext         — Provide Koin DI ke composables
 *   2. ThemePreferenceWrapper — Subscribe ke UserPreferences.themeMode Flow,
 *                            translate ThemeMode enum ke Boolean darkTheme
 *   3. neurodeckTheme      — Material 3 theming dengan darkTheme dari user choice
 *   4. AppNavHost          — Root navigation
 *
 * FIX: Sebelumnya neurodeckTheme dipanggil tanpa param → selalu pakai
 * isSystemInDarkTheme() default. User toggle Dark di Profile tidak berefek.
 * Sekarang wire ThemeMode flow → propagate ke darkTheme Boolean.
 */
@Composable
fun App() {
    KoinContext {
        ThemedAppContent()
    }
}

/**
 * Wrapper yang collect ThemeMode dari UserPreferences DataStore lalu apply
 * ke neurodeckTheme. Harus di dalam KoinContext supaya koinInject() bisa
 * resolve UserPreferencesRepository.
 *
 * Reactive: kalau user toggle theme di Profile screen, Flow emit ulang
 * → re-compose dengan darkTheme baru → seluruh app re-skin instant.
 */
@Composable
private fun ThemedAppContent() {
    // Resolve UserPreferencesRepository via Koin
    val userPrefs: UserPreferencesRepository = koinInject()

    // Observe theme mode dari DataStore (reactive)
    val themeMode by userPrefs.observeThemeMode()
        .collectAsState(initial = ThemeMode.DEFAULT)

    // Translate enum → Boolean
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.System -> systemDark
    }

    neurodeckTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()
        AppNavHost(navController = navController)
    }
}