package com.example.tabungin

import androidx.compose.runtime.Composable
import com.example.tabungin.presentation.navigation.AppNavHost
import com.example.tabungin.presentation.theme.TabungInTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        TabungInTheme {
            AppNavHost()
        }
    }
}
