package com.example.travelplanner

import androidx.compose.runtime.Composable
import com.example.travelplanner.presentation.theme.TravelPlannerTheme
import com.example.travelplanner.presentation.navigation.AppNavHost
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        TravelPlannerTheme {
            AppNavHost()
        }
    }
}
