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
import com.example.travelplanner.presentation.screens.trips.MyTripsScreen
import com.example.travelplanner.presentation.screens.expenses.ExpenseTrackerScreen
import com.example.travelplanner.presentation.screens.settings.SettingsScreen

// Definisi Rute Type-Safe
sealed class Route {
    @Serializable
    data object Home : Route()

    @Serializable
    data object GenerateTrip : Route()

    @Serializable
    data class TripResult(val tripId: String) : Route()

    @Serializable
    data object MyTrips : Route()

    @Serializable
    data class Expenses(val tripId: String? = null) : Route()

    @Serializable
    data object Settings : Route()
}

@Composable
fun AppNavHost(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        // Layar 1: Dasbor Utama (Home)
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

        // Layar 2: Input AI Itinerary (Generate Trip)
        composable<Route.GenerateTrip> {
            GenerateTripScreen(
                onNavigateBack = { navController.popBackStack() },
                onGenerateSuccess = { tripId ->
                    navController.navigate(Route.TripResult(tripId)) {
                        popUpTo(Route.GenerateTrip) { inclusive = true }
                    }
                }
            )
        }

        // Layar 3: Hasil AI & Timeline (Trip Result)
        composable<Route.TripResult> { backStackEntry ->
            val route: Route.TripResult = backStackEntry.toRoute()

            TripResultScreen(
                tripId = route.tripId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToExpenseTracker = {
                    navController.navigate(Route.Expenses(route.tripId))
                }
            )
        }

        // Layar 4: Semua Perjalanan (My Trips)
        composable<Route.MyTrips> {
            MyTripsScreen(
                onNavigateToTripDetail = { tripId ->
                    navController.navigate(Route.TripResult(tripId))
                }
            )
        }

        // Layar 5: Catatan Biaya (Expenses Tab)
        composable<Route.Expenses> { backStackEntry ->
            val route: Route.Expenses = backStackEntry.toRoute()
            ExpenseTrackerScreen(
                tripId = route.tripId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Layar 6: Pengaturan (Settings)
        composable<Route.Settings> {
            SettingsScreen(
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        }
    }
}