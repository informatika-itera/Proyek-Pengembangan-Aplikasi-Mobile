package com.example.foodsaver.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * NavHost untuk mengatur navigasi antar layar di FoodSaver.
 */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            // Placeholder untuk HomeScreen FoodSaver
            Text("Selamat Datang di FoodSaver!")
        }
        
        // Rute lainnya akan ditambahkan sesuai Sprint Plan
    }
}
