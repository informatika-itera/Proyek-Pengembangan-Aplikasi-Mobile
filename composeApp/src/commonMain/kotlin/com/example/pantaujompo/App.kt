package com.example.pantaujompo

import androidx.compose.runtime.Composable
import com.example.pantaujompo.presentation.navigation.AppNavHost
import com.example.pantaujompo.presentation.theme.PantauJompoTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        PantauJompoTheme {
            AppNavHost()
        }
    }
}