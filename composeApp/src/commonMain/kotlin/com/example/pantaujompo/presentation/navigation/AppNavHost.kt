package com.example.pantaujompo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pantaujompo.presentation.screens.home.BerandaDashboardScreen
import com.example.pantaujompo.presentation.screens.scanner.PemindaiGiziAIScreen
import com.example.pantaujompo.presentation.screens.history.IntegratedHistoryScreen
import com.example.pantaujompo.presentation.screens.articles.HealthLiteracyScreen
import com.example.pantaujompo.presentation.screens.profile.UserProfileScreen

// MAIN AppNavHost untuk mengelola 5 screen bottom nav
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Beranda.route,
        modifier = modifier
    ) {
        composable(Route.Beranda.route) {
            BerandaDashboardScreen(
                onNavigateToPemindai = { navController.navigate(Route.Pemindai.route) }
            )
        }
        composable(Route.Pemindai.route) {
            PemindaiGiziAIScreen()
        }
        composable(Route.Riwayat.route) {
            IntegratedHistoryScreen()
        }
        composable(Route.Artikel.route) {
            HealthLiteracyScreen()
        }
        composable(Route.Profil.route) {
            UserProfileScreen()
        }
    }
}