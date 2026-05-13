package com.example.Feelia

import androidx.compose.runtime.Composable
import com.example.Feelia.presentation.navigation.AppNavHost
import com.example.Feelia.presentation.theme.FeeliaTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        FeeliaTheme {
            AppNavHost()
        }
    }
}
