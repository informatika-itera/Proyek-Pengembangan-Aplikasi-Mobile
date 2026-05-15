package com.example.Roomie

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Business
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
import com.example.Roomie.presentation.facility.FacilityGridScreen
import com.example.Roomie.presentation.facility.RoomDetailScreen
import com.example.Roomie.presentation.report.ReportScreen
import com.example.Roomie.presentation.profile.ProfileScreen
import com.example.Roomie.presentation.theme.RoomieTheme
import com.example.Roomie.presentation.util.AppStrings
import org.jetbrains.compose.ui.tooling.preview.Preview

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Home : Screen("home", AppStrings.NAV_HOME, Icons.Default.Home)
    data object Facility : Screen("facility", AppStrings.NAV_FACILITY, Icons.Outlined.Business)
    data object Report : Screen("report", AppStrings.NAV_REPORT, Icons.Default.AddCircle)
    data object Profile : Screen("profile", AppStrings.NAV_PROFILE, Icons.Default.AccountCircle)
    data object RoomDetail : Screen("room_detail/{roomId}", "Detail Ruangan") {
        fun createRoute(roomId: String) = "room_detail/$roomId"
    }
    // Halaman lama FacilityDetail diganti dengan RoomDetail atau kita sesuaikan
}

@Composable
@Preview
fun App() {
    RoomieTheme {
        val navController = rememberNavController()
        val items = listOf(
            Screen.Home,
            Screen.Facility,
            Screen.Report,
            Screen.Profile
        )

        Scaffold(
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                // Hanya tampilkan BottomBar jika di halaman utama
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
                        onNavigateToMap = { navController.navigate(Screen.Facility.route) },
                        onNavigateToFacilityDetail = { id -> 
                            navController.navigate(Screen.RoomDetail.createRoute(id)) 
                        }
                    )
                }
                
                composable(Screen.Facility.route) {
                    FacilityGridScreen(
                        onNavigateToDetail = { roomId ->
                            navController.navigate(Screen.RoomDetail.createRoute(roomId))
                        }
                    )
                }
                
                composable(
                    route = Screen.RoomDetail.route,
                    arguments = listOf(navArgument("roomId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val roomId = backStackEntry.arguments?.getString("roomId")
                    RoomDetailScreen(
                        roomId = roomId,
                        onBack = { navController.popBackStack() }
                    )
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
