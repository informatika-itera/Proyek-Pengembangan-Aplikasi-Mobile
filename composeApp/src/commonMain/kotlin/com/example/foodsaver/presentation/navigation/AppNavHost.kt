package com.example.foodsaver.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.foodsaver.presentation.screens.addfood.AddFoodScreen
import com.example.foodsaver.presentation.screens.ai.AIAssistantScreen
import com.example.foodsaver.presentation.screens.calendar.CalendarScreen
import com.example.foodsaver.presentation.screens.detail.FoodDetailScreen
import com.example.foodsaver.presentation.screens.expiry.ExpiryScreen
import com.example.foodsaver.presentation.screens.home.HomeScreen
import com.example.foodsaver.presentation.screens.mealplan.MealPlannerScreen
import com.example.foodsaver.presentation.screens.profile.ProfileScreen
import com.example.foodsaver.presentation.screens.recipe.CookFromStockScreen
import com.example.foodsaver.presentation.screens.recipe.RecipeListScreen
import com.example.foodsaver.presentation.screens.recipe.RecipeRecommendationScreen
import com.example.foodsaver.presentation.screens.recipe.detail.RecipeDetailScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem("Home", "home", Icons.Filled.Inventory, Icons.Outlined.Inventory, "bottom_nav_home"),
        BottomNavItem("Expiry", "expiry", Icons.Filled.NotificationImportant, Icons.Outlined.NotificationImportant, "bottom_nav_expiry"),
        BottomNavItem("Resep", "recipe", Icons.Filled.RestaurantMenu, Icons.Outlined.RestaurantMenu, "bottom_nav_recipe"),
        BottomNavItem("Calendar", "calendar", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "bottom_nav_calendar"),
        BottomNavItem("Profile", "profile", Icons.Filled.Person, Icons.Outlined.Person, "bottom_nav_profile")
    )

    Scaffold(
        bottomBar = {
            val currentRoute = currentDestination?.route
            if (items.any { it.route == currentRoute?.split("?")?.get(0) || it.route == currentRoute?.split("/")?.get(0) }) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav")
                ) {
                    items.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            modifier = Modifier.testTag(item.testTag),
                            icon = { 
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon, 
                                    contentDescription = item.title 
                                ) 
                            },
                            label = { Text(item.title) },
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        val startRoute = navController.graph.findStartDestination().route ?: "home"
                                        popUpTo(startRoute) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    onAddFoodClick = { navController.navigate("add_food") },
                    onFoodClick = { id -> navController.navigate("detail/$id") },
                    onAIClick = { navController.navigate("ai") },
                    onCalendarClick = { navController.navigate("calendar") },
                    onCookFromStockClick = { navController.navigate("recipe") }
                )
            }

            composable("expiry") {
                ExpiryScreen(
                    onFoodClick = { id -> navController.navigate("detail/$id") },
                    onAddFoodClick = { navController.navigate("add_food") }
                )
            }

            composable("recipe") {
                CookFromStockScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToResult = { ids, manual, prioritize, pref ->
                        val idsString = if (ids.isEmpty()) "" else ids.joinToString(",")
                        val manualString = if (manual.isEmpty()) "" else manual.joinToString(",")
                        navController.navigate("recipe_recommendation?ids=$idsString&manual=$manualString&prioritize=$prioritize&pref=$pref")
                    },
                    onAddFoodClick = { navController.navigate("add_food") }
                )
            }

            composable(
                route = "recipe_recommendation?ids={ids}&manual={manual}&prioritize={prioritize}&pref={pref}",
                arguments = listOf(
                    navArgument("ids") { type = NavType.StringType; defaultValue = "" },
                    navArgument("manual") { type = NavType.StringType; defaultValue = "" },
                    navArgument("prioritize") { type = NavType.BoolType; defaultValue = true },
                    navArgument("pref") { type = NavType.StringType; defaultValue = "Praktis" }
                )
            ) { backStackEntry ->
                val ids = backStackEntry.arguments?.getString("ids")?.split(",")?.filter { it.isNotEmpty() }?.map { it.toLong() } ?: emptyList()
                val manual = backStackEntry.arguments?.getString("manual")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
                val prioritize = backStackEntry.arguments?.getBoolean("prioritize") ?: true
                val pref = backStackEntry.arguments?.getString("pref") ?: "Praktis"
                
                RecipeRecommendationScreen(
                    ingredientIds = ids,
                    manualIngredients = manual,
                    prioritizeExpired = prioritize,
                    preference = pref,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToHome = { 
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

            composable("calendar") {
                CalendarScreen(
                    onAddFoodClick = { navController.navigate("add_food") }
                )
            }

            composable("profile") {
                ProfileScreen(
                    onNavigateBack = { navController.popBackStack() }
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
                    initialText = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("meal_planner") {
                MealPlannerScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onRecipeClick = { recipeId -> navController.navigate("recipe_detail/$recipeId") }
                )
            }

            composable(
                route = "recipe_detail/{recipeId}",
                arguments = listOf(
                    navArgument("recipeId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
                RecipeDetailScreen(
                    recipeId = recipeId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

private data class BottomNavItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)
