package com.itera.news.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.itera.news.presentation.screens.detail.DetailScreen
import com.itera.news.presentation.screens.home.HomeScreen
import com.itera.news.presentation.screens.bookmark.BookmarkScreen
import com.itera.news.presentation.screens.add.AddEditScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route // Mulai langsung dari Home
    ) {
        composable(Screen.Splash.route) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Splash Screen") }
        }
        composable(Screen.Home.route) {
            HomeScreen(
                navigateToDetail = { url ->
                    navController.navigate(Screen.Detail.createRoute(url))
                },
                navigateToBookmark = {
                    navController.navigate(Screen.Bookmark.route)
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
            BookmarkScreen(
                onNavigateToDetail = { url ->
                    navController.navigate(Screen.Detail.createRoute(url))
                },
                onNavigateToAddEdit = { url ->
                    navController.navigate(Screen.AddEdit.createRoute(url))
                }
            )
        }
        composable(
            route = Screen.AddEdit.route,
            arguments = listOf(
                navArgument("articleUrl") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("articleUrl")
            val decodedUrl = encodedUrl?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
            AddEditScreen(
                url = decodedUrl,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.About.route) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("About Screen") }
        }
    }
}
