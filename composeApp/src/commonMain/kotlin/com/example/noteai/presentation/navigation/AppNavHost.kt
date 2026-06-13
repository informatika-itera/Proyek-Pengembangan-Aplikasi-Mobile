package com.example.noteai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.noteai.presentation.screens.MainScreen
import com.example.noteai.presentation.screens.auth.AuthScreen
import com.example.noteai.presentation.screens.recipe.AddEditRecipeScreen
import com.example.noteai.presentation.screens.recipe.RecipeDetailScreen
import com.example.noteai.presentation.screens.splash.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)

    NavHost(
        navController = navController,
        startDestination = Route.Splash,
        modifier = modifier
    ) {
        composable<Route.Splash> {
            SplashScreen(
                onNavigateToMain = { navigationActions.navigateToChat() },
                onNavigateToAuth = { navigationActions.navigateToAuth() }
            )
        }

        composable<Route.Auth> {
            AuthScreen(
                onAuthSuccess = {
                    navigationActions.navigateToChat()
                }
            )
        }

        composable<Route.Chat> {
            MainScreen(
                onRecipeClick = { id -> navigationActions.navigateToRecipeDetail(id) },
                onAddRecipeClick = { navigationActions.navigateToAddEditRecipe() },
                onLogout = {
                    navigationActions.navigateToAuth()
                }
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
        override fun navigateToSplash() {
            navController.navigate(Route.Splash) {
                popUpTo(0) { inclusive = true }
            }
        }

        override fun navigateToAuth() {
            navController.navigate(Route.Auth) {
                popUpTo(Route.Splash) { inclusive = true }
            }
        }

        override fun navigateToChat() {
            navController.navigate(Route.Chat) {
                popUpTo(Route.Splash) { inclusive = true }
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

interface NavigationActions {
    fun navigateToSplash()
    fun navigateToAuth()
    fun navigateToChat()
    fun navigateToPantry()
    fun navigateToRecipes()
    fun navigateToRecipeDetail(recipeId: Long)
    fun navigateToAddEditRecipe(recipeId: Long? = null)
    fun navigateBack()
}
