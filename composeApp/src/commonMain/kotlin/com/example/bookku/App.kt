package com.example.bookku

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bookku.data.local.datastore.UserPreferences
import com.example.bookku.presentation.navigation.AppNavHost
import com.example.bookku.presentation.theme.bookkuTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinContext {
        val userPreferences = koinInject<UserPreferences>()
        val isDarkMode by userPreferences.isDarkMode.collectAsState(initial = false)
        
        bookkuTheme(darkTheme = isDarkMode) {
            AppNavHost()
        }
    }
}


