package com.studyhub

import androidx.compose.runtime.Composable
import com.studyhub.presentation.navigation.AppNavHost
import com.studyhub.presentation.theme.StudyHubTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        StudyHubTheme {
            AppNavHost()
        }
    }
}

