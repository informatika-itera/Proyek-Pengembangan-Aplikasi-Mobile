package com.example.bookku

import androidx.compose.runtime.Composable
import com.example.bookku.presentation.navigation.AppNavHost
import com.example.bookku.presentation.theme.bookkuTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        bookkuTheme {
            AppNavHost()
        }
    }
}


