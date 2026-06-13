package com.example.fitgen

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.example.fitgen.data.local.datastore.UserPreferences
import com.example.fitgen.presentation.navigation.AppNavHost
import com.example.fitgen.presentation.theme.FitGenTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun App() {
    KoinContext {
        val userPreferences: UserPreferences = koinInject()
        val isDarkMode by userPreferences.isDarkMode.collectAsState(initial = isSystemInDarkTheme())

        FitGenTheme(darkTheme = isDarkMode) {
            AppNavHost()
        }
    }
}
