package com.example.rosea.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier // 👈 Tambahan import untuk Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rosea.presentation.screens.ai.AIAssistantScreen
import com.example.rosea.presentation.screens.cart.CartScreen
import com.example.rosea.presentation.screens.detail.DetailScreen
import com.example.rosea.presentation.screens.home.HomeScreen
import com.example.rosea.presentation.screens.profile.ProfileScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Routes.HOME,
    modifier: Modifier = Modifier // 👈 Tambahan parameter agar jarak layar pas dengan menu bawah
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier // 👈 Modifier dipasang di sini
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                // 👇 Hanya tersisa Detail, karena Cart, AI, dan Profil pindah ke Menu Bawah
                onNavigateToDetail = { productId ->
                    navController.navigate(Routes.createDetailRoute(productId))
                }
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

        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}