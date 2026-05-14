package com.example.mybawanggacha

import androidx.compose.runtime.Composable
import com.example.mybawanggacha.presentation.navigation.AppNavHost
import com.example.mybawanggacha.presentation.theme.MBGTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        MBGTheme {
            AppNavHost()
        }
    }
}
