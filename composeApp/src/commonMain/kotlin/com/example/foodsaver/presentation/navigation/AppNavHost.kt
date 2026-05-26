package com.example.foodsaver.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
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
import com.example.foodsaver.presentation.theme.PrimaryGreen

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Expiry : BottomNavItem("expiry", "Expiry", Icons.Filled.History, Icons.Outlined.History)
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
    }
}

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val nestedNavController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Expiry,
        BottomNavItem.Calendar,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
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
                                popUpTo(item.route) {
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
                    onAIClick = { rootNavController.navigate("ai") }
                )
            }
            composable(BottomNavItem.Expiry.route) {
                ExpiryScreen(
                    onFoodClick = { id -> rootNavController.navigate("detail/$id") }
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
