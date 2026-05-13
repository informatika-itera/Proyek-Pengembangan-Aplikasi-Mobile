package com.example.pantaujompo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pantaujompo.presentation.screens.home.DashboardScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Dashboard,
        modifier = modifier
    ) {
        composable<Route.Dashboard> {
            DashboardScreen(
                onNavigateToAdd = { /* Nanti kita isi */ },
                onNavigateToDetail = { id -> /* Nanti kita isi */ }
            )
        }
        // Layar AddEditActivity dan ActivityDetail akan kita tambahkan setelah layar ini jalan
    }
}