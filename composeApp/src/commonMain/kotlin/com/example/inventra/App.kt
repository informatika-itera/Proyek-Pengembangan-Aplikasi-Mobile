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

@Composable
fun App() {
    val isSystemDark = isSystemInDarkTheme()
    val isDarkThemeState = remember { mutableStateOf(isSystemDark) }

    CompositionLocalProvider(LocalThemeIsDark provides isDarkThemeState) {
        InventRaTheme(darkTheme = isDarkThemeState.value) {
            Surface(
                color = androidx.compose.material3.MaterialTheme.colorScheme.background
            ) {
                AppNavHost()
            }
        }
    }
}