package com.mywallet.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.mywallet.presentation.screens.stats.StatisticsScreen
import com.mywallet.presentation.screens.savings.SavingsGoalScreen
import com.mywallet.presentation.screens.settings.SettingsScreen
import com.mywallet.presentation.screens.settings.SecurityScreen
import com.mywallet.presentation.screens.settings.HelpScreen
import com.mywallet.presentation.screens.settings.AboutScreen

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    // Bottom bar sekarang selalu muncul karena kita langsung berada di halaman dalam aplikasi
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(navController) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
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
        startDestination = Screen.Home.route, // MUTLAK: Aplikasi langsung start di Beranda (Home)
        modifier = modifier
    ) {

        // Halaman 1: Langsung Beranda (Home)
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

        // Halaman 2: Detail Transaksi
        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("transactionId") ?: return@composable
            DetailScreen(
                transactionId = id,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Screen.EditTransaction.createRoute(id)) }
            )
        }

        // Halaman 3: Edit Transaksi
        composable(
            route = Screen.EditTransaction.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("transactionId") ?: return@composable
            EditTransactionScreen(
                transactionId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Halaman 4: Tambah Transaksi
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Halaman 5: Riwayat Transaksi
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.TransactionDetail.createRoute(id))
                }
            )
        }

        // Halaman 6: Statistik Keuangan
        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.TransactionDetail.createRoute(id))
                }
            )
        }

        // Halaman 7: Target Tabungan
        composable(Screen.SavingsGoal.route) {
            SavingsGoalScreen()
        }

        // Halaman 8: Profil User
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToSettings = { title ->
                    navController.navigate(Screen.SettingsDetail.createRoute(title))
                }
            )
        }

        // Halaman 9: Detail Pengaturan (Sub-menu dari Profil)
        composable(
            route = Screen.SettingsDetail.route,
            arguments = listOf(navArgument("title") { type = NavType.StringType })
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            if (title == "Pengaturan Akun") {
                SettingsScreen(onNavigateBack = { navController.popBackStack() })
            } else {
                when (title) {
                    "Keamanan" -> SecurityScreen(onNavigateBack = { navController.popBackStack() })
                    "Pusat Bantuan" -> HelpScreen(onNavigateBack = { navController.popBackStack() })
                    "Tentang Aplikasi" -> AboutScreen(onNavigateBack = { navController.popBackStack() })
                    else -> {
                        Scaffold(
                            topBar = {
                                CenterAlignedTopAppBar(
                                    title = { Text(title, color = Color.White) },
                                    navigationIcon = {
                                        IconButton(onClick = { navController.popBackStack() }) {
                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                                        }
                                    },
                                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkNavy)
                                )
                            }
                        ) { p ->
                            Box(modifier = Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) {
                                Text("Halaman $title akan segera hadir!")
                            }
                        }
                    }
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
        BottomNavItem("Target", Screen.SavingsGoal.route, Icons.Default.TrackChanges),
        BottomNavItem("Statistik", Screen.Statistics.route, Icons.Default.BarChart),
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

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
    )
}