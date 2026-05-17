package com.example.Roomie

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Dashboard
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
import com.example.Roomie.domain.model.UserRole
import com.example.Roomie.presentation.AppViewModel
import com.example.Roomie.presentation.auth.LoginScreen
import com.example.Roomie.presentation.home.HomeScreen
import com.example.Roomie.presentation.facility.BuildingListScreen
import com.example.Roomie.presentation.facility.FacilityGridScreen
import com.example.Roomie.presentation.facility.RoomDetailScreen
import com.example.Roomie.presentation.facility.BookingScreen
import com.example.Roomie.presentation.facility.SearchRoomScreen
import com.example.Roomie.presentation.facility.ScheduleScreen
import com.example.Roomie.presentation.help.HelpScreen
import com.example.Roomie.presentation.report.ReportScreen
import com.example.Roomie.presentation.report.AllReportsScreen
import com.example.Roomie.presentation.profile.ProfileScreen
import com.example.Roomie.presentation.admin.AdminDashboardScreen
import com.example.Roomie.presentation.theme.RoomieTheme
import com.example.Roomie.presentation.util.AppStrings
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Login : Screen("login", "Masuk", Icons.AutoMirrored.Filled.Login)
    data object Home : Screen("home", AppStrings.NAV_HOME, Icons.Default.Home)
    data object Facility : Screen("facility", AppStrings.NAV_FACILITY, Icons.Outlined.Business)
    data object RoomSelection : Screen("room_selection", "Pilih Ruangan")
    data object SearchRoom : Screen("search_room", "Cari Ruangan")
    data object AllReports : Screen("all_reports", "Semua Laporan")
    data object Booking : Screen("booking/{roomId}", "Pinjam Ruangan") {
        fun createRoute(roomId: String) = "booking/$roomId"
    }
    data object Schedule : Screen("schedule", AppStrings.HOME_SCHEDULE)
    data object Help : Screen("help", AppStrings.HOME_HELP)
    data object Report : Screen("report", AppStrings.NAV_REPORT, Icons.Default.AddCircle)
    data object Profile : Screen("profile", AppStrings.NAV_PROFILE, Icons.Default.AccountCircle)
    data object AdminDashboard : Screen("admin_dashboard", "Admin", Icons.Default.Dashboard)
    data object RoomDetail : Screen("room_detail/{roomId}", "Detail Ruangan") {
        fun createRoute(roomId: String) = "room_detail/$roomId"
    }
}

@Composable
@Preview
fun App(
    viewModel: AppViewModel = koinViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val navController = rememberNavController()

    val darkTheme = when (themeMode) {
        1 -> false // Light
        2 -> true  // Dark
        else -> isSystemInDarkTheme() // System
    }
    
    RoomieTheme(darkTheme = darkTheme) {
        // Define navigation items based on role
        val navItems = remember(currentUser) {
            when (currentUser?.role) {
                UserRole.STUDENT -> listOf(
                    Screen.Home,
                    Screen.Facility,
                    Screen.Report,
                    Screen.Profile
                )
                UserRole.ADMIN -> listOf(
                    Screen.AdminDashboard,
                    Screen.Facility,
                    Screen.Profile
                )
                else -> emptyList()
            }
        }

        Scaffold(
            bottomBar = {
                if (currentUser != null) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    
                    val showBottomBar = navItems.any { it.route == currentDestination?.route }
                    
                    if (showBottomBar) {
                        NavigationBar {
                            navItems.forEach { screen ->
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
            }
        ) { innerPadding ->
            val startRoute = remember(currentUser) {
                when {
                    currentUser == null -> Screen.Login.route
                    currentUser?.role == UserRole.ADMIN -> Screen.AdminDashboard.route
                    else -> Screen.Home.route
                }
            }

            NavHost(
                navController = navController,
                startDestination = startRoute,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(onLoginSuccess = {})
                }

                composable(Screen.Home.route) {
                    HomeScreen(
                        onNavigateToSearch = { navController.navigate(Screen.SearchRoom.route) },
                        onNavigateToSchedule = { navController.navigate(Screen.Schedule.route) },
                        onNavigateToHelp = { navController.navigate(Screen.Help.route) },
                        onNavigateToReport = { navController.navigate(Screen.Report.route) },
                        onNavigateToAllReports = { navController.navigate(Screen.AllReports.route) }
                    )
                }

                composable(Screen.AllReports.route) {
                    AllReportsScreen(onBack = { navController.popBackStack() })
                }
                
                composable(Screen.SearchRoom.route) {
                    SearchRoomScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateToDetail = { roomId ->
                            navController.navigate(Screen.RoomDetail.createRoute(roomId))
                        }
                    )
                }

                composable(
                    route = Screen.Booking.route,
                    arguments = listOf(navArgument("roomId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
                    BookingScreen(
                        roomId = roomId,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Schedule.route) {
                    ScheduleScreen(onBack = { navController.popBackStack() })
                }

                composable(Screen.Help.route) {
                    HelpScreen(onBack = { navController.popBackStack() })
                }
                
                composable(Screen.Facility.route) {
                    BuildingListScreen(
                        onBuildingClick = { building ->
                            if (building.id == "GKU2") {
                                navController.navigate(Screen.RoomSelection.route)
                            }
                        }
                    )
                }

                composable(Screen.RoomSelection.route) {
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
                        onBack = { navController.popBackStack() },
                        onNavigateToBooking = { id ->
                            navController.navigate(Screen.Booking.createRoute(id))
                        }
                    )
                }
                
                composable(Screen.Report.route) {
                    ReportScreen()
                }
                
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        onLogout = { viewModel.logout() },
                        currentThemeMode = themeMode,
                        onThemeChange = { viewModel.setThemeMode(it) }
                    )
                }

                composable(Screen.AdminDashboard.route) {
                    AdminDashboardScreen(onBack = { navController.popBackStack() })
                }
            }
        }
        
        // Auto-navigation on auth change
        LaunchedEffect(currentUser) {
            if (currentUser == null) {
                if (navController.currentDestination?.route != Screen.Login.route) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            } else {
                val currentRoute = navController.currentDestination?.route
                if (currentRoute == Screen.Login.route || currentRoute == null) {
                    val dest = if (currentUser?.role == UserRole.ADMIN) Screen.AdminDashboard.route else Screen.Home.route
                    navController.navigate(dest) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
        }
    }
}
