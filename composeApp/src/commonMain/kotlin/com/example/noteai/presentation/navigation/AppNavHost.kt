package com.example.noteai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.noteai.presentation.screens.MainScreen
import com.example.noteai.presentation.screens.recipe.AddEditRecipeScreen
import com.example.noteai.presentation.screens.recipe.RecipeDetailScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)
    
    NavHost(
        navController = navController,
        startDestination = Route.Chat,
        modifier = modifier
    ) {
        composable<Route.Chat> {
            MainScreen(
                onRecipeClick = { id -> navigationActions.navigateToRecipeDetail(id) },
                onAddRecipeClick = { navigationActions.navigateToAddEditRecipe() }
            )
        }

        composable<Route.RecipeDetail> { backStackEntry ->
            val route: Route.RecipeDetail = backStackEntry.toRoute()
            RecipeDetailScreen(
                recipeId = route.recipeId,
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateToEdit = { id -> navigationActions.navigateToAddEditRecipe(id) }
            )
        }

        composable<Route.AddEditRecipe> { backStackEntry ->
            val route: Route.AddEditRecipe = backStackEntry.toRoute()
            AddEditRecipeScreen(
                recipeId = route.recipeId,
                onNavigateBack = { navigationActions.navigateBack() }
            )
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToChat() {
            navController.navigate(Route.Chat) {
                popUpTo(Route.Chat) { inclusive = true }
            }
        }
        
        override fun navigateToPantry() {
            navController.navigate(Route.Pantry)
        }
        
        override fun navigateToRecipes() {
            navController.navigate(Route.Recipes)
        }
        
        override fun navigateToRecipeDetail(recipeId: Long) {
            navController.navigate(Route.RecipeDetail(recipeId))
        }
        
        override fun navigateToAddEditRecipe(recipeId: Long?) {
            navController.navigate(Route.AddEditRecipe(recipeId))
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}
