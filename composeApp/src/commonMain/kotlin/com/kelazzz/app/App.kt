package com.kelazzz.app

import androidx.compose.runtime.Composable
import com.kelazzz.app.presentation.navigation.AppNavHost
import com.kelazzz.app.presentation.theme.KelazZzTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        KelazZzTheme {
            AppNavHost()
        }
    }
}
