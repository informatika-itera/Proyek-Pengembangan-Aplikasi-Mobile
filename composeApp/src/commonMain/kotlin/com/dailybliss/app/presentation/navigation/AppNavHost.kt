package com.dailybliss.app.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.dailybliss.app.presentation.screens.ai.AIAssistantScreen
import com.dailybliss.app.presentation.screens.addnote.CreateMomentScreen
import com.dailybliss.app.presentation.screens.detail.MomentDetailScreen
import com.dailybliss.app.presentation.screens.home.DashboardScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val actions = remember(navController) { NavigationActionsImpl(navController) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val hideBottomBarScreens = listOf(
        Route.CreateMoment::class,
        Route.MomentDetail::class
    )
    
    val isAIAssistant = currentDestination?.hierarchy?.any { it.hasRoute(Route.AIAssistant::class) } == true
    
    val showBottomBar = hideBottomBarScreens.none { route ->
        currentDestination?.hierarchy?.any { it.hasRoute(route) } == true
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color.Gray,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute(Route.Home::class) } == true,
                        onClick = { actions.navigateToHome() },
                        icon = { 
                            Icon(
                                if (currentDestination?.hierarchy?.any { it.hasRoute(Route.Home::class) } == true) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            ) 
                        },
                        label = { Text("Jurnal") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                    
                    NavigationBarItem(
                        selected = isAIAssistant,
                        onClick = { actions.navigateToAIAssistant() },
                        icon = { 
                            Icon(
                                if (isAIAssistant) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                                contentDescription = "AI Assistant"
                            ) 
                        },
                        label = { Text("Asisten AI") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        // AIAssistant manages its own bottom padding dynamically to stay flush with the keyboard
        val navHostPadding = if (isAIAssistant) {
            PaddingValues(top = paddingValues.calculateTopPadding(), bottom = 0.dp)
        } else {
            paddingValues
        }

        NavHost(
            navController = navController,
            startDestination = Route.Home,
            modifier = modifier.padding(navHostPadding)
        ) {
            composable<Route.Home> {
                DashboardScreen(
                    onNavigateToCreateMoment = { actions.navigateToCreateMoment() },
                    onNavigateToMomentDetail = { id -> actions.navigateToMomentDetail(id) }
                )
            }

            composable<Route.CreateMoment> {
                CreateMomentScreen(
                    onNavigateBack = { actions.navigateBack() }
                )
            }

            composable<Route.MomentDetail> { backStackEntry ->
                val route: Route.MomentDetail = backStackEntry.toRoute()
                MomentDetailScreen(
                    momentId = route.momentId,
                    onNavigateBack = { actions.navigateBack() }
                )
            }

            composable<Route.AIAssistant> {
                AIAssistantScreen(
                    onNavigateBack = { actions.navigateBack() }
                )
            }
            
            composable<Route.Settings> {
                // Simplified settings for now
            }
        }
    }
}

private class NavigationActionsImpl(private val navController: NavHostController) : NavigationActions {
    override fun navigateToHome() {
        navController.navigate(Route.Home) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    override fun navigateToCreateMoment(momentId: Long?) {
        navController.navigate(Route.CreateMoment(momentId))
    }

    override fun navigateToMomentDetail(momentId: Long) {
        navController.navigate(Route.MomentDetail(momentId))
    }

    override fun navigateToAIAssistant() {
        navController.navigate(Route.AIAssistant) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    override fun navigateToSettings() {
        navController.navigate(Route.Settings)
    }

    override fun navigateBack() {
        navController.popBackStack()
    }
}
