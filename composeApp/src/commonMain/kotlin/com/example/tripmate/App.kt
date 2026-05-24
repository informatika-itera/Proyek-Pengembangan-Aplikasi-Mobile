package com.example.tripmate

import androidx.compose.runtime.Composable
import com.example.tripmate.presentation.navigation.AppNavHost
import com.example.tripmate.presentation.theme.TripMateTheme

@Composable
fun App() {
    TripMateTheme {
        AppNavHost()
    }
}
