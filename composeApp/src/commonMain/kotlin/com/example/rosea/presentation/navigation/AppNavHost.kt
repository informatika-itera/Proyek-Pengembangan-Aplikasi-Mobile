package com.example.rosea.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rosea.presentation.screens.ai.AIAssistantScreen
import com.example.rosea.presentation.screens.cart.CartScreen
import com.example.rosea.presentation.screens.detail.DetailScreen
import com.example.rosea.presentation.screens.home.HomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Routes.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                // Tombol Floating Action Button Tas Belanja sekarang aktif!
                onNavigateToAddNote = { navController.navigate(Routes.CART) },
                onNavigateToDetail = { productId ->
                    navController.navigate(Routes.createDetailRoute(productId))
                },
                onNavigateToAI = { navController.navigate(Routes.AI_ASSISTANT) }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
            DetailScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CART) {
            CartScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.AI_ASSISTANT) {
            AIAssistantScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}