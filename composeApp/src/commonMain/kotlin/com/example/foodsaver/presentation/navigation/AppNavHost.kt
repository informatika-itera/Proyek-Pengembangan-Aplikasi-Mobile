package com.example.foodsaver.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
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
import com.example.foodsaver.presentation.screens.profile.ProfileScreen
import com.example.foodsaver.presentation.screens.recipe.CookFromStockScreen
import com.example.foodsaver.presentation.screens.recipe.RecipeRecommendationScreen
import com.example.foodsaver.presentation.theme.PrimaryGreen

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Expiry : BottomNavItem("expiry", "Expiry", Icons.Filled.History, Icons.Outlined.History)
    object Recipe : BottomNavItem("recipe", "Resep", Icons.Filled.RestaurantMenu, Icons.Outlined.RestaurantMenu)
    object Calendar : BottomNavItem("calendar", "Calendar", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth)
    object Profile : BottomNavItem("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                rootNavController = navController
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

        // Fitur Masak dari Stok (Sekarang bisa diakses dari Bottom Nav juga)
        composable("recipe_selection") {
            CookFromStockScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResult = { ingredientIds, manualIngredients, prioritizeExpired, preference ->
                    val idsString = if (ingredientIds.isEmpty()) "none" else ingredientIds.joinToString(",")
                    val manualString = if (manualIngredients.isEmpty()) "none" else manualIngredients.joinToString(",")
                    navController.navigate("recipe_result/$idsString/$manualString/$prioritizeExpired/$preference")
                }
            )
        }

        composable(
            route = "recipe_result/{ids}/{manual}/{prioritize}/{pref}",
            arguments = listOf(
                navArgument("ids") { type = NavType.StringType },
                navArgument("manual") { type = NavType.StringType },
                navArgument("prioritize") { type = NavType.BoolType },
                navArgument("pref") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val ids = backStackEntry.arguments?.getString("ids")?.let { 
                if (it == "none") emptyList() else it.split(",").mapNotNull { id -> id.toLongOrNull() } 
            } ?: emptyList()
            val manual = backStackEntry.arguments?.getString("manual")?.let {
                if (it == "none") emptyList() else it.split(",")
            } ?: emptyList()
            val prioritize = backStackEntry.arguments?.getBoolean("prioritize") ?: true
            val pref = backStackEntry.arguments?.getString("pref") ?: "Praktis"
            
            RecipeRecommendationScreen(
                ingredientIds = ids,
                manualIngredients = manual,
                prioritizeExpired = prioritize,
                preference = pref,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val nestedNavController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Expiry,
        BottomNavItem.Recipe,
        BottomNavItem.Calendar,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                items.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                if (isSelected) item.selectedIcon else item.unselectedIcon, 
                                contentDescription = item.title 
                            ) 
                        },
                        label = { Text(item.title) },
                        selected = isSelected,
                        onClick = {
                            nestedNavController.navigate(item.route) {
                                val startDestination = nestedNavController.graph.findStartDestination()
                                popUpTo(startDestination.route ?: BottomNavItem.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryGreen,
                            selectedTextColor = PrimaryGreen,
                            indicatorColor = PrimaryGreen.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nestedNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onAddFoodClick = { rootNavController.navigate("add_food") },
                    onFoodClick = { id -> rootNavController.navigate("detail/$id") },
                    onCalendarClick = { nestedNavController.navigate(BottomNavItem.Calendar.route) },
                    onAIClick = { rootNavController.navigate("ai") },
                    onCookFromStockClick = { nestedNavController.navigate(BottomNavItem.Recipe.route) }
                )
            }
            composable(BottomNavItem.Expiry.route) {
                ExpiryScreen(
                    onFoodClick = { id -> rootNavController.navigate("detail/$id") }
                )
            }
            composable(BottomNavItem.Recipe.route) {
                CookFromStockScreen(
                    onNavigateBack = { /* No back button in bottom nav tab */ },
                    onNavigateToResult = { ingredientIds, manualIngredients, prioritizeExpired, preference ->
                        val idsString = if (ingredientIds.isEmpty()) "none" else ingredientIds.joinToString(",")
                        val manualString = if (manualIngredients.isEmpty()) "none" else manualIngredients.joinToString(",")
                        rootNavController.navigate("recipe_result/$idsString/$manualString/$prioritizeExpired/$preference")
                    }
                )
            }
            composable(BottomNavItem.Calendar.route) {
                CalendarScreen()
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }
        }
    }
}
