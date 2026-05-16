package com.example.bridgebit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.History
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
import com.example.bridgebit.presentation.navigation.AppNavHost
import com.example.bridgebit.presentation.navigation.Route
import com.example.bridgebit.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext

// Data class untuk membantu definisi item navbar bawah
data class BottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: Route
)

@Composable
fun App() {
    KoinContext {
        NoteAITheme {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            // Daftar halaman yang akan tampil di bottom bar
            val navigationItems = remember {
                listOf(
                    BottomNavItem("Riwayat", Icons.Default.History, Route.Dashboard),
                    BottomNavItem("Terjemah", Icons.Default.Translate, Route.Workspace()),
                    BottomNavItem("Vault", Icons.Default.Star, Route.Vault),
                    BottomNavItem("Statistik", Icons.Default.BarChart, Route.Insights),
                    BottomNavItem("Pengaturan", Icons.Default.Settings, Route.Settings)
                )
            }

            // Cari tahu apakah halaman saat ini termasuk salah satu item navbar bawah
            // Jika user membuka DetailScreen atau AIScreen, Navbar otomatis tersembunyi
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
                                                // Menghindari penumpukan tumpukan backstack halaman
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
                // Memasukkan paddingValues navbar ke NavHost agar UI tidak terpotong navbar bawah
                AppNavHost(
                    navController = navController,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}