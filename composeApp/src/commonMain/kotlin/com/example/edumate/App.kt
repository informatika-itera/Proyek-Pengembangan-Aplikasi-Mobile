package com.example.edumate

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.edumate.presentation.navigation.AppNavHost
import com.example.edumate.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            // Tambahkan rememberNavController() di sini
            val navController = rememberNavController()
            AppNavHost(navController = navController)
        }
    }
}