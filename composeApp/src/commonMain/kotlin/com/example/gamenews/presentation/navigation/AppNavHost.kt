package com.example.gamenews.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.gamenews.presentation.screens.ai.AIAssistantScreen
import com.example.gamenews.presentation.screens.home.HomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)

    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomeScreen(
                onNavigateToDetail = { gameId -> navigationActions.navigateToGameDetail(gameId) },
                onNavigateToAI = { navigationActions.navigateToAIAssistant() }
            )
        }

        composable<Route.AIAssistant> {
            AIAssistantScreen(
                onNavigateBack = { navigationActions.navigateBack() }
            )
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToHome() {
            navController.navigate(Route.Home) {
                popUpTo(Route.Home) { inclusive = true }
            }
        }

        override fun navigateToGameDetail(gameId: Long) {
            navController.navigate(Route.GameDetail(gameId))
        }

        override fun navigateToAIAssistant() {
            navController.navigate(Route.AIAssistant)
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}