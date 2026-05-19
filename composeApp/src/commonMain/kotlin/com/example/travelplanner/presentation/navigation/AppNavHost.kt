package com.example.travelplanner.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import com.example.travelplanner.presentation.screens.home.HomeScreen
import com.example.travelplanner.presentation.screens.planner.GenerateTripScreen
import com.example.travelplanner.presentation.screens.result.TripResultScreen

// Definisi Rute Type-Safe (Aman dari Kesalahan Ketik)
sealed class Route {
    @Serializable
    data object Home : Route()

    @Serializable
    data object GenerateTrip : Route()

    @Serializable
    data class TripResult(val tripId: String) : Route()
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        // Layar 1: Dasbor Utama
        composable<Route.Home> {
            HomeScreen(
                onNavigateToGenerateTrip = {
                    navController.navigate(Route.GenerateTrip)
                },
                onNavigateToTripDetail = { tripId ->
                    navController.navigate(Route.TripResult(tripId))
                }
            )
        }

        // Layar 2: Input AI Itinerary
        composable<Route.GenerateTrip> {
            GenerateTripScreen(
                onNavigateBack = { navController.popBackStack() },
                onGenerateSuccess = { tripId ->
                    // Setelah berhasil generate, pergi ke Result Screen dan hancurkan layar Generate dari backstack
                    navController.navigate(Route.TripResult(tripId)) {
                        popUpTo(Route.GenerateTrip) { inclusive = true }
                    }
                }
            )
        }

        // Layar 3: Hasil AI & Timeline
        composable<Route.TripResult> { backStackEntry ->
            // Mengambil argumen tripId dari rute secara aman
            val route: Route.TripResult = backStackEntry.toRoute()

            TripResultScreen(
                tripId = route.tripId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToExpenseTracker = {
                    // TODO: Akan dihubungkan saat fitur Expense Tracker selesai dibangun
                }
            )
        }
    }
}