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
import com.example.travelplanner.presentation.screens.profile.ProfileScreen
import com.example.travelplanner.presentation.screens.splash.SplashScreen

sealed class Route {
    @Serializable data object Splash : Route()
    @Serializable data object Home : Route()
    @Serializable data object GenerateTrip : Route()
    @Serializable data class  TripResult(val tripId: String) : Route()
    @Serializable data object MyTrips : Route()
    @Serializable data class  Expenses(val tripId: String? = null) : Route()
    @Serializable data object Profile : Route()
    @Serializable data object Settings : Route()
}

@Composable
fun AppNavHost(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    isEnglish: Boolean,
    onToggleLanguage: () -> Unit,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = Route.Splash, modifier = modifier) {

        composable<Route.Splash> {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Home> {
            HomeScreen(
                onNavigateToGenerateTrip = { navController.navigate(Route.GenerateTrip) },
                onNavigateToTripDetail   = { navController.navigate(Route.TripResult(it)) },
                onNavigateToMyTrips      = { navController.navigate(Route.MyTrips) }
            )
        }

        composable<Route.GenerateTrip> {
            GenerateTripScreen(
                onNavigateBack      = { navController.popBackStack() },
                onGenerateSuccess   = { tripId ->
                    navController.navigate(Route.TripResult(tripId)) {
                        popUpTo(Route.GenerateTrip) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.TripResult> { backStackEntry ->
            val route: Route.TripResult = backStackEntry.toRoute()
            TripResultScreen(
                tripId = route.tripId,
                onNavigateBack            = { navController.popBackStack() },
                onNavigateToExpenseTracker = { navController.navigate(Route.Expenses(route.tripId)) }
            )
        }

        composable<Route.MyTrips> {
            MyTripsScreen(
                onNavigateToTripDetail = { navController.navigate(Route.TripResult(it)) }
            )
        }

        composable<Route.Expenses> { backStackEntry ->
            val route: Route.Expenses = backStackEntry.toRoute()
            ExpenseTrackerScreen(
                tripId              = route.tripId,
                onNavigateBack      = { navController.popBackStack() },
                onNavigateToTrips   = if (route.tripId == null) {
                    {
                        navController.navigate(Route.MyTrips) {
                            popUpTo(Route.Home) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                } else null
            )
        }

        // ── Profil tab ────────────────────────────────────────────────
        composable<Route.Profile> {
            ProfileScreen(
                onNavigateToSettings = { navController.navigate(Route.Settings) },
                onNavigateToTrips    = {
                    navController.navigate(Route.MyTrips) {
                        popUpTo(Route.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToExpenses = {
                    navController.navigate(Route.Expenses(null)) {
                        popUpTo(Route.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // ── Settings ──────────────────────────────────────────────────
        composable<Route.Settings> {
            SettingsScreen(
                isDarkMode       = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                isEnglish        = isEnglish,
                onToggleLanguage = onToggleLanguage,
                onNavigateBack   = { navController.popBackStack() }
            )
        }
    }
}
