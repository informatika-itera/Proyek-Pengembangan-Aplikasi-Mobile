package com.example.tripmate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.tripmate.presentation.navigation.AppNavHost
import com.example.tripmate.presentation.theme.TripMateTheme

@Composable
fun App() {
    var isDarkMode by remember { mutableStateOf(false) }

    TripMateTheme(darkTheme = isDarkMode) {
        AppNavHost(
            isDarkMode = isDarkMode,
            onToggleDarkMode = { isDarkMode = it }
        )
    }
}