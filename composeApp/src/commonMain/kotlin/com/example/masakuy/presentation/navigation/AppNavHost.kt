package com.example.masakuy.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.masakuy.presentation.screens.auth.LoginScreen
import com.example.masakuy.presentation.screens.detail.DetailScreen
import com.example.masakuy.presentation.screens.favorite.FavoriteScreen
import com.example.masakuy.presentation.screens.home.HomeScreen
import com.example.masakuy.presentation.screens.recommendation.RecommendationScreen
import com.example.masakuy.presentation.screens.search.SearchScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Home.route) {
            HomeScreen(
                onRecommendationClick = { budget ->
                    navController.navigate(Routes.Recommendation.createRoute(budget))
                },
                onRecipeClick = { recipeId ->
                    navController.navigate(Routes.Detail.createRoute(recipeId))
                },
                onFavoriteClick = {
                    navController.navigate(Routes.Favorite.route)
                },
                onSearchClick = {
                    navController.navigate(Routes.Search.route)
                }
            )
        }

        composable(Routes.Recommendation.route) { backStackEntry ->
            val budget = backStackEntry.arguments?.getString("budget")?.toIntOrNull() ?: 0
            RecommendationScreen(
                budget = budget,
                onRecipeClick = { recipeId ->
                    navController.navigate(Routes.Detail.createRoute(recipeId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.Detail.route) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            DetailScreen(
                recipeId = recipeId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.Favorite.route) {
            FavoriteScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(Routes.Detail.createRoute(recipeId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.Search.route) {
            SearchScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(Routes.Detail.createRoute(recipeId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}