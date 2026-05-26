package com.example.travelplanner

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.travelplanner.presentation.theme.TravelPlannerTheme
import com.example.travelplanner.presentation.navigation.AppNavHost
import com.example.travelplanner.presentation.navigation.Route
import org.koin.compose.KoinContext

@Composable
fun App() {
    // Global dark mode preference state
    var isDarkMode by remember { mutableStateOf(false) }

    KoinContext {
        TravelPlannerTheme(darkTheme = isDarkMode) {
            val navController = rememberNavController()
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route

            // Standard type-safe route matching: show BottomBar only on core tabs
            val showBottomBar = when (currentRoute?.substringAfterLast(".")) {
                "Home", "MyTrips", "Expenses", "Settings" -> true
                else -> false
            }

            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        BottomNavigationBar(
                            navController = navController,
                            currentRoute = currentRoute
                        )
                    }
                }
            ) { paddingValues ->
                AppNavHost(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { isDarkMode = !isDarkMode },
                    navController = navController,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: String?
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val tabs = listOf(
            Triple("Utama", Route.Home, Icons.Default.Home),
            Triple("Perjalanan", Route.MyTrips, Icons.Default.Map),
            Triple("Keuangan", Route.Expenses(null), Icons.Default.AccountBalanceWallet),
            Triple("Pengaturan", Route.Settings, Icons.Default.Settings)
        )

        tabs.forEach { (label, route, icon) ->
            val routeClassName = route::class.simpleName ?: ""
            // Match class name ignoring type-safe parameter queries
            val isSelected = currentRoute?.substringAfterLast(".")?.substringBefore("?") == routeClassName

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(route) {
                            popUpTo(Route.Home) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp)) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                )
            )
        }
    }
}
