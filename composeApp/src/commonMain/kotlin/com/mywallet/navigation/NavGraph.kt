package com.mywallet.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.mywallet.theme.DarkNavy
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mywallet.presentation.screens.add.AddTransactionScreen
import com.mywallet.presentation.screens.detail.DetailScreen
import com.mywallet.presentation.screens.home.HomeScreen
import com.mywallet.presentation.screens.add.EditTransactionScreen
import com.mywallet.presentation.screens.history.HistoryScreen
import com.mywallet.presentation.screens.profile.ProfileScreen
import com.mywallet.presentation.screens.splash.SplashScreen

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(navController) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0) // Disable default inset handling to avoid double padding
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.TransactionDetail.createRoute(id))
                },
                onNavigateToAdd = {
                    navController.navigate(Screen.AddTransaction.route)
                }
            )
        }
        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(navArgument(NavArgs.TRANSACTION_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt(NavArgs.TRANSACTION_ID) ?: return@composable
            DetailScreen(
                transactionId = id,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Screen.EditTransaction.createRoute(id)) }
            )
        }
        composable(
            route = Screen.EditTransaction.route,
            arguments = listOf(navArgument(NavArgs.TRANSACTION_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt(NavArgs.TRANSACTION_ID) ?: return@composable
            EditTransactionScreen(
                transactionId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.TransactionDetail.createRoute(id))
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToSettings = { title ->
                    navController.navigate(Screen.SettingsDetail.createRoute(title))
                }
            )
        }
        composable(
            route = Screen.SettingsDetail.route,
            arguments = listOf(navArgument(NavArgs.TITLE) { type = NavType.StringType })
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString(NavArgs.TITLE) ?: ""
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(title) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkNavy, titleContentColor = Color.White)
                    )
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Halaman $title akan segera hadir!", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Beranda", Screen.Home.route, Icons.Default.Home),
        BottomNavItem("Riwayat", Screen.History.route, Icons.Default.History),
        BottomNavItem("Profil", Screen.Profile.route, Icons.Default.Person)
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
