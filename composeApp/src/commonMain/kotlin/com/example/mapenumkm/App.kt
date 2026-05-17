package com.example.mapenumkm

import androidx.compose.runtime.Composable
import com.example.mapenumkm.presentation.navigation.AppNavHost
import com.example.mapenumkm.presentation.theme.MaPenTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        MaPenTheme {
            AppNavHost()
        }
    }
}
