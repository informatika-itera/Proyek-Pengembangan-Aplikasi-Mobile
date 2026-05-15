package com.itera.news.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.itera.news.presentation.screen.AboutScreen
import com.itera.news.presentation.screen.BookmarkScreen
import com.itera.news.presentation.screen.DetailScreen
import com.itera.news.presentation.screen.HomeScreen
import com.itera.news.presentation.screen.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        // Hapus Splash dari tumpukan backstack agar saat user tekan 'Back', tidak kembali ke Splash
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                navigateToDetail = { url ->
                    navController.navigate(Screen.Detail.createRoute(url))
                }
            )
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("articleUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val articleUrl = backStackEntry.arguments?.getString("articleUrl") ?: ""
            DetailScreen(
                encodedUrl = articleUrl,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Bookmark.route) {
            BookmarkScreen()
        }
        composable(Screen.About.route) {
            AboutScreen()
        }
    }
}