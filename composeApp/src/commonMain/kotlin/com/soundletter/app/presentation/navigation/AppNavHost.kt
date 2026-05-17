package com.soundletter.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.soundletter.app.presentation.screens.SplashContent
import com.soundletter.app.presentation.screens.home.HomeScreen
import com.soundletter.app.presentation.screens.search.SearchScreen
import com.soundletter.app.presentation.screens.compose.ComposeScreen
import com.soundletter.app.presentation.screens.history.HistoryScreen
import com.soundletter.app.presentation.screens.detail.DetailMessageScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashContent(
                onTimeout = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCompose = { navController.navigate(Screen.Compose.route) },
                onNavigateToDetail = { id -> navController.navigate(Screen.DetailMessage.createRoute(id)) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) }
            )
        }
        
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id -> navController.navigate(Screen.DetailMessage.createRoute(id)) }
            )
        }
        
        composable(Screen.Compose.route) {
            ComposeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id -> navController.navigate(Screen.DetailMessage.createRoute(id)) }
            )
        }
        
        composable(
            route = Screen.DetailMessage.route,
            arguments = listOf(navArgument("messageId") { type = NavType.StringType })
        ) { backStackEntry ->
            val messageId = backStackEntry.arguments?.getString("messageId") ?: ""
            DetailMessageScreen(
                messageId = messageId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
