package com.studymate

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.studymate.presentation.navigation.AppNavHost
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        MaterialTheme {
            AppNavHost()
        }
    }
}
