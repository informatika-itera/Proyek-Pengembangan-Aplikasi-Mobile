package com.example.pocketguard

import androidx.compose.runtime.Composable
import com.example.pocketguard.presentation.navigation.AppNavHost
import com.example.pocketguard.presentation.theme.PocketGuardTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        PocketGuardTheme {
            AppNavHost()
        }
    }
}
