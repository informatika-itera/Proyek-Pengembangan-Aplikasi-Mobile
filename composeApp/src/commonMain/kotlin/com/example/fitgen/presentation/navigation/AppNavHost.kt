package com.example.fitgen.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.fitgen.presentation.screens.ai.AIAssistantScreen
import com.example.fitgen.presentation.screens.ai.DynamicWorkoutScreen
import com.example.fitgen.presentation.screens.home.HomeDashboardScreen
import com.example.fitgen.presentation.screens.home.HomeScreen
import com.example.fitgen.presentation.screens.nutrition.AddMealScreen
import com.example.fitgen.presentation.screens.nutrition.NutritionScreen
import com.example.fitgen.presentation.screens.profile.ProfileScreen
import com.example.fitgen.presentation.screens.workout.AddWorkoutScreen
import com.example.fitgen.presentation.screens.workout.WorkoutListScreen

private val topLevelRoutes = listOf(
    Route.Home::class,
    Route.Dashboard::class,
    Route.WorkoutList::class,
    Route.Nutrition::class,
    Route.Profile::class
)

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = topLevelRoutes.any { routeClass ->
        currentDestination?.hasRoute(routeClass) == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute(Route.Home::class) == true,
                        onClick  = { navigationActions.navigateToHome() },
                        icon = {
                            Icon(
                                if (currentDestination?.hasRoute(Route.Home::class) == true)
                                    Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Beranda"
                            )
                        },
                        label = { Text("Beranda") }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute(Route.WorkoutList::class) == true,
                        onClick  = { navigationActions.navigateToWorkoutList() },
                        icon = {
                            Icon(
                                if (currentDestination?.hasRoute(Route.WorkoutList::class) == true)
                                    Icons.Filled.FitnessCenter else Icons.Outlined.FitnessCenter,
                                contentDescription = "Latihan"
                            )
                        },
                        label = { Text("Latihan") }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute(Route.Nutrition::class) == true,
                        onClick  = { navigationActions.navigateToNutrition() },
                        icon = {
                            Icon(
                                if (currentDestination?.hasRoute(Route.Nutrition::class) == true)
                                    Icons.Filled.RestaurantMenu else Icons.Outlined.RestaurantMenu,
                                contentDescription = "Nutrisi"
                            )
                        },
                        label = { Text("Nutrisi") }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute(Route.Profile::class) == true,
                        onClick  = { navigationActions.navigateToProfile() },
                        icon = {
                            Icon(
                                if (currentDestination?.hasRoute(Route.Profile::class) == true)
                                    Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Profil"
                            )
                        },
                        label = { Text("Profil") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Route.Home,
            modifier         = modifier.padding(innerPadding)
        ) {
            composable<Route.Home> {
                HomeDashboardScreen(
                    onNavigateToWorkout        = { navigationActions.navigateToWorkoutList() },
                    onNavigateToNutrition      = { navigationActions.navigateToNutrition() },
                    onNavigateToDynamicWorkout = { navigationActions.navigateToDynamicWorkout() }
                )
            }
            composable<Route.AIAssistant> { backStackEntry ->
                val route: Route.AIAssistant = backStackEntry.toRoute()
                AIAssistantScreen(
                    noteId         = null,
                    initialText    = route.initialText,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onApplyResult  = null
                )
            }
            composable<Route.Dashboard> {
                HomeDashboardScreen(
                    onNavigateToWorkout        = { navigationActions.navigateToWorkoutList() },
                    onNavigateToNutrition      = { navigationActions.navigateToNutrition() },
                    onNavigateToDynamicWorkout = { navigationActions.navigateToDynamicWorkout() }
                )
            }
            composable<Route.WorkoutList> {
                WorkoutListScreen(
                    onNavigateToAddWorkout = { navigationActions.navigateToAddWorkout() },
                    onNavigateToDetail     = { }
                )
            }
            composable<Route.AddWorkout> {
                AddWorkoutScreen(onNavigateBack = { navigationActions.navigateBack() })
            }
            composable<Route.Nutrition> {
                NutritionScreen(onNavigateToAddMeal = { navigationActions.navigateToAddMeal() })
            }
            composable<Route.AddMeal> {
                AddMealScreen(onNavigateBack = { navigationActions.navigateBack() })
            }
            composable<Route.Profile> {
                ProfileScreen(
                    onNavigateBack = { navigationActions.navigateBack() }
                )
            }
            composable<Route.DynamicWorkout> {
                DynamicWorkoutScreen(
                    onNavigateBack = { navigationActions.navigateBack() }
                )
            }
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToHome() {
            navController.navigate(Route.Home) { popUpTo(Route.Home) { inclusive = true } }
        }
        override fun navigateToDashboard() {
            navController.navigate(Route.Dashboard) { popUpTo(Route.Dashboard) { inclusive = true } }
        }
        override fun navigateToAIAssistant(initialText: String?) =
            navController.navigate(Route.AIAssistant(initialText))
        override fun navigateToDynamicWorkout() = navController.navigate(Route.DynamicWorkout)
        override fun navigateToWorkoutList() {
            navController.navigate(Route.WorkoutList) { popUpTo(Route.WorkoutList) { inclusive = true } }
        }
        override fun navigateToAddWorkout() = navController.navigate(Route.AddWorkout)
        override fun navigateToNutrition() {
            navController.navigate(Route.Nutrition) { popUpTo(Route.Nutrition) { inclusive = true } }
        }
        override fun navigateToAddMeal() = navController.navigate(Route.AddMeal)
        override fun navigateToProfile() {
            navController.navigate(Route.Profile) { popUpTo(Route.Profile) { inclusive = true } }
        }
        override fun navigateBack() = navController.popBackStack().let {}
    }
}