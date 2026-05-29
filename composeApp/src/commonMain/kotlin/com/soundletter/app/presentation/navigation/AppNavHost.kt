package com.soundletter.app.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.soundletter.app.presentation.screens.SplashContent
import com.soundletter.app.presentation.screens.home.HomeScreen
import com.soundletter.app.presentation.screens.search.SearchScreen
import com.soundletter.app.presentation.screens.compose.ComposeScreen
import com.soundletter.app.presentation.screens.history.HistoryScreen
import com.soundletter.app.presentation.screens.detail.DetailMessageScreen
import com.soundletter.app.presentation.screens.settings.SettingsScreen
import com.soundletter.app.presentation.screens.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

    val mainScreens = listOf(
        Screen.Home,
        Screen.Search,
        Screen.History,
        Screen.Settings
    )

    val showBottomBar = mainScreens.any { it.route == currentDestination?.route }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = if (isDarkMode) Color(0xFF121212) else Color.White,
                    tonalElevation = 8.dp
                ) {
                    mainScreens.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = when (screen) {
                                        Screen.Home -> Icons.Default.Home
                                        Screen.Search -> Icons.Default.Search
                                        Screen.History -> Icons.Default.History
                                        Screen.Settings -> Icons.Default.Settings
                                        else -> Icons.Default.Home
                                    },
                                    contentDescription = screen.route,
                                    tint = if (selected) MaterialTheme.colorScheme.primary 
                                           else if (isDarkMode) Color.White.copy(alpha = 0.6f) 
                                           else Color.DarkGray
                                )
                            },
                            label = { 
                                Text(
                                    screen.route.replaceFirstChar { it.uppercase() },
                                    color = if (selected) MaterialTheme.colorScheme.primary 
                                            else if (isDarkMode) Color.White.copy(alpha = 0.6f) 
                                            else Color.DarkGray
                                ) 
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = if (isDarkMode) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
            }
        ) {
            composable(Screen.Splash.route) {
                SplashContent(
                    onTimeout = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToCompose = { navController.navigate(Screen.Compose.route) },
                    onNavigateToDetail = { id -> navController.navigate(Screen.DetailMessage.createRoute(id)) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    navController = navController
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { id -> navController.navigate(Screen.DetailMessage.createRoute(id)) }
                )
            }

            composable(Screen.Compose.route) {
                ComposeScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSuccess = { isSynced: Boolean ->
                        navController.previousBackStackEntry?.savedStateHandle?.set("compose_success", true)
                        navController.previousBackStackEntry?.savedStateHandle?.set("compose_is_synced", isSynced)
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { id -> navController.navigate(Screen.DetailMessage.createRoute(id)) }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.DetailMessage.route,
                arguments = listOf(navArgument("messageId") { type = NavType.StringType })
            ) { backStackEntry ->
                val messageId = backStackEntry.arguments?.getString("messageId") ?: ""
                DetailMessageScreen(
                    messageId = messageId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
