package com.example.masakuy.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.masakuy.core.network.NetworkMonitor
import com.example.masakuy.theme.OrangeMain
import com.example.masakuy.presentation.components.OfflineBanner
import com.example.masakuy.presentation.screens.detail.DetailScreen
import com.example.masakuy.presentation.screens.favorite.FavoriteScreen
import com.example.masakuy.presentation.screens.home.HomeScreen
import com.example.masakuy.presentation.screens.profile.ProfileScreen
import com.example.masakuy.presentation.screens.recommendation.RecommendationScreen
import com.example.masakuy.presentation.screens.search.SearchScreen
import java.net.URLDecoder

data class BottomNavItem(val label: String, val route: String, val emoji: String)

val bottomNavItems = listOf(
    BottomNavItem("Home",    Routes.Home.route,     "\uD83C\uDFE0"),
    BottomNavItem("Cari",    Routes.Search.route,   "\uD83D\uDD0D"),
    BottomNavItem("Favorit", Routes.Favorite.route, "\u2764\uFE0F"),
    BottomNavItem("Akun",    "profile",             "\uD83D\uDC64"),
)

val bottomNavRoutes = setOf(
    Routes.Home.route,
    Routes.Search.route,
    Routes.Favorite.route,
    "profile"
)

@Composable
fun AppNavHost(
    isDarkMode: Boolean = true,
    onDarkModeToggle: (Boolean) -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val context = LocalContext.current
    val networkMonitor = remember { NetworkMonitor(context) }
    val isOnline by networkMonitor.isOnline.collectAsState()

    val showBottomBar = currentRoute in bottomNavRoutes ||
            currentRoute?.startsWith("recommendation") == true

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Text(item.emoji, fontSize = if (selected) 22.sp else 20.sp) },
                            label = { Text(item.label, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = OrangeMain,
                                selectedTextColor   = OrangeMain,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor      = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            OfflineBanner(isOnline = isOnline)
            NavHost(navController = navController, startDestination = Routes.Home.route) {


                composable(Routes.Home.route) {
                    HomeScreen(
                        onRecommendationClick = { budget ->
                            navController.navigate(Routes.Recommendation.createRoute(budget))
                        },
                        onRecipeClick = { recipeId ->
                            navController.navigate(Routes.Detail.createRoute(recipeId, recipeId, 0))
                        },
                        onFavoriteClick = { navController.navigate(Routes.Favorite.route) },
                        onSearchClick = { navController.navigate(Routes.Search.route) }
                    )
                }

                composable(Routes.Recommendation.route) { backStackEntry ->
                    val budget = backStackEntry.arguments?.getString("budget")?.toIntOrNull() ?: 0
                    RecommendationScreen(
                        budget = budget,
                        onRecipeClick = { recipeId, recipeName ->
                            navController.navigate(Routes.Detail.createRoute(recipeId, recipeName, budget))
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(Routes.Detail.route) { backStackEntry ->
                    val recipeId   = backStackEntry.arguments?.getString("recipeId") ?: ""
                    val recipeName = URLDecoder.decode(backStackEntry.arguments?.getString("recipeName") ?: "", "UTF-8")
                    val budget     = backStackEntry.arguments?.getString("budget")?.toIntOrNull() ?: 0
                    DetailScreen(
                        recipeId   = recipeId,
                        recipeName = recipeName,
                        budget     = budget,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(Routes.Favorite.route) {
                    FavoriteScreen(
                        onRecipeClick = { recipeId, recipeName ->
                            navController.navigate(Routes.Detail.createRoute(recipeId, recipeName, 0))
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(Routes.Search.route) {
                    SearchScreen(
                        onRecipeClick = { recipeId ->
                            navController.navigate(Routes.Detail.createRoute(recipeId, recipeId, 0))
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable("profile") {
                    ProfileScreen(
                        isDarkMode = isDarkMode,
                        onDarkModeToggle = onDarkModeToggle
                    )
                }
            }
        }
    }
}