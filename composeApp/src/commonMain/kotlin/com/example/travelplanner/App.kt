package com.example.travelplanner

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.koin.compose.KoinContext
import com.example.travelplanner.presentation.navigation.AppNavHost

@Composable
fun App() {
    // KoinContext memastikan seluruh tree Compose ini dapat mengakses Dependency Injection
    KoinContext {
        // Saya menggunakan MaterialTheme bawaan untuk sementara agar kompilasi aman.
        // Nanti kita bisa membuat TravelPlannerTheme khusus.
        MaterialTheme {
            AppNavHost()
        }
    }
}