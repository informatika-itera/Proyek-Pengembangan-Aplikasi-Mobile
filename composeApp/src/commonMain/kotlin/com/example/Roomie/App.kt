package com.example.Roomie

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.Roomie.presentation.home.HomeScreen
import com.example.Roomie.presentation.map.MapScreen
import com.example.Roomie.presentation.facility.FacilityDetailScreen
import com.example.Roomie.presentation.report.ReportScreen
import com.example.Roomie.presentation.profile.ProfileScreen
import com.example.Roomie.presentation.theme.RoomieTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Home : Screen("home", "Beranda", Icons.Default.Home)
    data object Map : Screen("map", "Peta", Icons.Default.LocationOn)
    data object Report : Screen("report", "Lapor", Icons.Default.AddCircle)
    data object Profile : Screen("profile", "Profil", Icons.Default.AccountCircle)
    data object FacilityDetail : Screen("facility_detail/{facilityId}", "Detail") {
        fun createRoute(facilityId: String) = "facility_detail/$facilityId"
    }
}

@Composable
@Preview
fun App() {
    RoomieTheme {
        val navController = rememberNavController()
        val items = listOf(
            Screen.Home,
            Screen.Map,
            Screen.Report,
            Screen.Profile
        )

        Scaffold(
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                // Hanya tampilkan BottomBar jika di halaman utama (bukan detail)
                val showBottomBar = items.any { it.route == currentDestination?.route }
                
                if (showBottomBar) {
                    NavigationBar {
                        items.forEach { screen ->
                            NavigationBarItem(
                                icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                                label = { Text(screen.title) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        val startDest = navController.graph.findStartDestination().route
                                        if (startDest != null) {
                                            popUpTo(startDest) {
                                                saveState = true
                                            }
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onNavigateToMap = { navController.navigate(Screen.Map.route) },
                        onNavigateToFacilityDetail = { id -> 
                            navController.navigate(Screen.FacilityDetail.createRoute(id)) 
                        }
                    )
                }
                
                composable(Screen.Map.route) {
                    MapScreen()
                }
                
                composable(
                    route = Screen.FacilityDetail.route,
                    arguments = listOf(navArgument("facilityId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val facilityId = backStackEntry.arguments?.getString("facilityId")
                    FacilityDetailScreen(facilityId)
                }
                
                composable(Screen.Report.route) {
                    ReportScreen()
                }
                
                composable(Screen.Profile.route) {
                    ProfileScreen()
                }
            }
        }
    }
}
