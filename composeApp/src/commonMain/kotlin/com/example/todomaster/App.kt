package com.example.todomaster

import androidx.compose.runtime.Composable
import com.example.todomaster.presentation.navigation.AppNavHost
import com.example.todomaster.presentation.theme.TodoMasterTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        TodoMasterTheme {
            AppNavHost()
        }
    }
}
