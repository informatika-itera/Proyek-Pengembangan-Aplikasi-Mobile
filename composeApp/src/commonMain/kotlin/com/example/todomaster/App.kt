package com.example.todomaster

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.todomaster.presentation.navigation.AppNavHost
import com.example.todomaster.presentation.theme.TodoMasterTheme
import org.koin.compose.KoinContext

object ThemeConfig {
    var isDarkTheme by mutableStateOf(false)
}

@Composable
fun App() {
    KoinContext {
        TodoMasterTheme(darkTheme = ThemeConfig.isDarkTheme) {
            AppNavHost()
        }
    }
}