package com.example.noteai

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.noteai.presentation.navigation.AppNavHost

@Composable
fun App() {
    MaterialTheme {
        AppNavHost()
    }
}