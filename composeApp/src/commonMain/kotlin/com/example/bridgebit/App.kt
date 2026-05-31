package com.example.bridgebit

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bridgebit.data.local.datastore.UserPreferences
import com.example.bridgebit.presentation.navigation.AppNavHost
import com.example.bridgebit.presentation.navigation.Route
import com.example.bridgebit.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

// Data class untuk membantu definisi item navbar bawah
data class BottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: Route
)

@Composable
fun App() {
    KoinContext {
        val userPreferences: UserPreferences = koinInject()
        val isDarkModePref by userPreferences.isDarkMode.collectAsState(initial = isSystemInDarkTheme())

        NoteAITheme(darkTheme = isDarkModePref) {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            // DAFTAR HALAMAN YANG SUDAH DIPERBAIKI (Tidak ada error parameter lagi)
            val navigationItems = remember {
                listOf(
                    BottomNavItem("Riwayat", Icons.Default.History, Route.Dashboard),
                    BottomNavItem("Terjemah", Icons.Default.Edit, Route.Workspace(translationId = null)),
                    BottomNavItem("Vault", Icons.Default.Star, Route.Vault),
                    BottomNavItem("Statistik", Icons.Default.BarChart, Route.Insights),
                    BottomNavItem("Pengaturan", Icons.Default.Settings, Route.Settings)
                )
            }

            val showBottomBar = navigationItems.any { item ->
                currentDestination?.hasRoute(item.route::class) == true
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (showBottomBar) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            navigationItems.forEach { item ->
                                val isSelected = currentDestination?.hasRoute(item.route::class) == true
                                NavigationBarItem(
                                    selected = isSelected,
                                    label = { Text(item.label) },
                                    icon = { Icon(item.icon, contentDescription = item.label) },
                                    onClick = {
                                        if (!isSelected) {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
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
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }
                    }
                }
            ) { paddingValues ->
                AppNavHost(
                    navController = navController,
                    modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                )
            }
        }
    }
}