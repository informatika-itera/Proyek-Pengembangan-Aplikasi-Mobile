package com.example.sholatyuk.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sholatyuk.presentation.screens.home.HomeScreen
import com.example.sholatyuk.presentation.screens.prayer.PrayerScreen
import com.example.sholatyuk.presentation.screens.doa.DoaScreen
import com.example.sholatyuk.presentation.screens.islamai.IslamAIScreen
// 👇 Tambahkan import ProfileScreen
import com.example.sholatyuk.presentation.screens.profile.ProfileScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomeScreen(
                onNavigateToShalat = {
                    navController.navigate(Route.Shalat) { popUpTo(Route.Home) { saveState = true }; launchSingleTop = true; restoreState = true }
                },
                onNavigateToDoa = {
                    navController.navigate(Route.Doa) { popUpTo(Route.Home) { saveState = true }; launchSingleTop = true; restoreState = true }
                },
                onNavigateToIslamAI = {
                    navController.navigate(Route.IslamAI) { popUpTo(Route.Home) { saveState = true }; launchSingleTop = true; restoreState = true }
                },
                // 👇 Tambahkan aksi navigasi ke profil
                onNavigateToProfile = {
                    navController.navigate(Route.Profile) { launchSingleTop = true }
                }
            )
        }
        composable<Route.Shalat> {
            PrayerScreen(
                onNavigateToHome = {
                    navController.navigate(Route.Home) { popUpTo(Route.Home) { saveState = true }; launchSingleTop = true; restoreState = true }
                },
                onNavigateToDoa = {
                    navController.navigate(Route.Doa) { popUpTo(Route.Home) { saveState = true }; launchSingleTop = true; restoreState = true }
                },
                onNavigateToIslamAI = {
                    navController.navigate(Route.IslamAI) { popUpTo(Route.Home) { saveState = true }; launchSingleTop = true; restoreState = true }
                }
            )
        }
        composable<Route.Doa> {
            DoaScreen(
                onNavigateToHome = {
                    navController.navigate(Route.Home) { popUpTo(Route.Home) { saveState = true }; launchSingleTop = true; restoreState = true }
                },
                onNavigateToShalat = {
                    navController.navigate(Route.Shalat) { popUpTo(Route.Home) { saveState = true }; launchSingleTop = true; restoreState = true }
                },
                onNavigateToIslamAI = {
                    navController.navigate(Route.IslamAI) { popUpTo(Route.Home) { saveState = true }; launchSingleTop = true; restoreState = true }
                }
            )
        }
        composable<Route.IslamAI> {
            IslamAIScreen(
                onNavigateToHome = {
                    navController.navigate(Route.Home) { popUpTo(Route.Home) { saveState = true }; launchSingleTop = true; restoreState = true }
                },
                onNavigateToShalat = {
                    navController.navigate(Route.Shalat) { popUpTo(Route.Home) { saveState = true }; launchSingleTop = true; restoreState = true }
                },
                onNavigateToDoa = {
                    navController.navigate(Route.Doa) { popUpTo(Route.Home) { saveState = true }; launchSingleTop = true; restoreState = true }
                }
            )
        }

        // 👇 Tambahkan halaman Profile baru di sini
        composable<Route.Profile> {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}