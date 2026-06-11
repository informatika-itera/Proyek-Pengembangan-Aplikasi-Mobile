package com.example.inventra

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.inventra.presentation.navigation.AppNavHost
import com.example.inventra.presentation.theme.InventRaTheme
import com.example.inventra.presentation.theme.LocalThemeIsDark

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.inventra.core.localization.Language
import com.example.inventra.core.localization.LocalLanguage
import com.example.inventra.core.localization.ProvideStrings
import com.example.inventra.data.local.datastore.UserPreferences
import com.example.inventra.presentation.components.AppBackground
import com.example.inventra.presentation.navigation.AppNavHost
import com.example.inventra.presentation.theme.InventRaTheme
import com.example.inventra.presentation.theme.LocalThemeIsDark
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import org.koin.compose.getKoin

@Composable
fun App() {
    val isSystemDark = isSystemInDarkTheme()
    val isDarkThemeState = remember { mutableStateOf(isSystemDark) }
    val languageState = remember { mutableStateOf(Language.INDONESIAN) }
    
    val koin = getKoin()
    val userPrefs = remember { koin.get<UserPreferences>() }

    LaunchedEffect(Unit) {
        userPrefs.isDarkMode.collect { isDarkThemeState.value = it }
    }
    
    LaunchedEffect(Unit) {
        userPrefs.language.collect { code ->
            languageState.value = Language.fromCode(code)
        }
    }

    CompositionLocalProvider(
        LocalThemeIsDark provides isDarkThemeState,
        LocalLanguage provides languageState
    ) {
        ProvideStrings(language = languageState.value) {
            InventRaTheme(darkTheme = isDarkThemeState.value) {
                AppBackground(isDark = isDarkThemeState.value) {
                    AppNavHost()
                }
            }
        }
    }
}