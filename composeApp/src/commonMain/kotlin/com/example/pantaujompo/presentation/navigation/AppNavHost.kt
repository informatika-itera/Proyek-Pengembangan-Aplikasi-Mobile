package com.example.pantaujompo.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

// Import SEMUA Layar Lo Di Sini
import com.example.pantaujompo.presentation.screens.home.DashboardScreen
import com.example.pantaujompo.presentation.screens.pemindai.PemindaiScreen
import com.example.pantaujompo.presentation.screens.riwayat.RiwayatScreen
import com.example.pantaujompo.presentation.screens.artikel.ArtikelScreen
import com.example.pantaujompo.presentation.screens.profil.ProfilScreen
import com.example.pantaujompo.presentation.screens.addedit.AddEditActivityScreen


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val bottomNavItems = listOf(
        BottomNavItem("Beranda", Route.Beranda, Icons.Default.Home),
        BottomNavItem("Pemindai", Route.Pemindai, Icons.Default.CameraAlt),
        BottomNavItem("Riwayat", Route.Riwayat, Icons.Default.ListAlt),
        BottomNavItem("Artikel", Route.Artikel, Icons.Default.Article),
        BottomNavItem("Profil", Route.Profil, Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Bottom Bar hilang kalau lagi di layar Form
    val showBottomBar = currentDestination?.hierarchy?.any {
        it.route?.contains("AddEditActivity") == true || it.route?.contains("ActivityDetail") == true
    } != true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route?.substringBefore("?") == item.route::class.qualifiedName
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().route!!) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Beranda,
            modifier = modifier.padding(innerPadding)
        ) {
            // 5 LAYAR UTAMA
            composable<Route.Beranda> {
                DashboardScreen(onNavigateToAdd = { navController.navigate(Route.AddEditActivity(null)) })
            }
            composable<Route.Pemindai> { PemindaiScreen() }

            // INI YANG TADI ERROR BRAY, UDAH GUE FIX!
            composable<Route.Riwayat> {
                RiwayatScreen(
                    onNavigateToEdit = { id -> navController.navigate(Route.AddEditActivity(id)) }
                )
            }

            composable<Route.Artikel> { ArtikelScreen() }
            composable<Route.Profil> { ProfilScreen() }

            // LAYAR FORM TAMBAH/EDIT DATA
            composable<Route.AddEditActivity> { backStackEntry ->
                val route: Route.AddEditActivity = backStackEntry.toRoute()
                AddEditActivityScreen(
                    activityId = route.id,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

data class BottomNavItem(val title: String, val route: Route, val icon: androidx.compose.ui.graphics.vector.ImageVector)