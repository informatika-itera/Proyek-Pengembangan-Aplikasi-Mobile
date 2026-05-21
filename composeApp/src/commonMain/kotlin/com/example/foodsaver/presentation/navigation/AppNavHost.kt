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
import com.example.foodsaver.presentation.screens.mealplan.MealPlannerScreen
import com.example.foodsaver.presentation.screens.recipe.RecipeListScreen
import com.example.foodsaver.presentation.screens.recipe.detail.RecipeDetailScreen

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
                onAIClick = { navController.navigate("ai") },
                onMealPlannerClick = { navController.navigate("meal_planner") },
                onRecommendClick = { ingredients -> 
                    val query = ingredients.joinToString(",")
                    navController.navigate("recipe_list?ingredients=$query") 
                }
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

        composable(
            route = "recipe_list?ingredients={ingredients}",
            arguments = listOf(
                navArgument("ingredients") { 
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val ingredients = backStackEntry.arguments?.getString("ingredients")?.split(",") ?: emptyList()
            RecipeListScreen(
                initialIngredients = ingredients,
                onRecipeClick = { recipeId -> navController.navigate("recipe_detail/$recipeId") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "recipe_detail/{recipeId}",
            arguments = listOf(
                navArgument("recipeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable
            RecipeDetailScreen(
                recipeId = recipeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("meal_planner") {
            MealPlannerScreen(
                onNavigateBack = { navController.popBackStack() },
                onRecipeClick = { recipeId -> navController.navigate("recipe_detail/$recipeId") }
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
