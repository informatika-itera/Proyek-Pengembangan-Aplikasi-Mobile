package com.dailybliss.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.dailybliss.app.data.local.datastore.UserPreferences
import com.dailybliss.app.presentation.navigation.AppNavHost
import com.dailybliss.app.presentation.theme.DailyBlissTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinContext {
        val userPreferences: UserPreferences = koinInject()
        val colorTheme by userPreferences.colorTheme.collectAsState(initial = "Sage Green")
        
        DailyBlissTheme(themeName = colorTheme) {
            AppNavHost()
        }
    }
}

