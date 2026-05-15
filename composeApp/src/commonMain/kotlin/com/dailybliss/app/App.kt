package com.dailybliss.app

import androidx.compose.runtime.Composable
import com.dailybliss.app.presentation.navigation.AppNavHost
import com.dailybliss.app.presentation.theme.DailyBlissTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        DailyBlissTheme {
            AppNavHost()
        }
    }
}

