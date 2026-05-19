package com.example.foodsaver.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.foodsaver.presentation.screens.addfood.AddFoodScreen
import com.example.foodsaver.presentation.screens.ai.AIAssistantScreen
import com.example.foodsaver.presentation.screens.detail.FoodDetailScreen
import com.example.foodsaver.presentation.screens.home.HomeScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onAddFoodClick = { navController.navigate("add_food") },
                onFoodClick = { id -> navController.navigate("detail/$id") },
                onAIClick = { navController.navigate("ai") }
            )
        }

        composable(
            route = "add_food?foodId={foodId}",
            arguments = listOf(
                navArgument("foodId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val foodId = backStackEntry.arguments?.getLong("foodId")?.takeIf { it != -1L }
            AddFoodScreen(
                foodId = foodId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "detail/{foodId}",
            arguments = listOf(
                navArgument("foodId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val foodId = backStackEntry.arguments?.getLong("foodId") ?: return@composable
            FoodDetailScreen(
                foodId = foodId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate("add_food?foodId=$id") }
            )
        }

        composable("ai") {
            AIAssistantScreen(
                noteId = null,
                initialText = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
