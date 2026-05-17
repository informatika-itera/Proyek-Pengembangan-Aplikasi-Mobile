package com.example.musickeep.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musickeep.presentation.screens.addmusic.AddMusicScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.AddMusic, // Mulai dari AddMusic dulu untuk demo Sprint 2
        modifier = modifier
    ) {
        composable<Route.AddMusic> {
            AddMusicScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        // Route.Home akan diimplementasikan nanti di Sprint 3
    }
}
