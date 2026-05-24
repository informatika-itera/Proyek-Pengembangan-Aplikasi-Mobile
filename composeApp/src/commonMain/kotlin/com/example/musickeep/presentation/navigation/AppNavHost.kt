package com.example.musickeep.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.musickeep.presentation.screens.addmusic.AddMusicScreen
import com.example.musickeep.presentation.screens.home.HomeScreen
import com.example.musickeep.presentation.screens.detail.MusicDetailScreen
import com.example.musickeep.presentation.screens.settings.SettingsScreen

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
                onNavigateToAddMusic = { navController.navigate(Route.AddMusic()) },
                onNavigateToDetail = { id -> navController.navigate(Route.MusicDetail(id)) },
                onNavigateToSettings = { navController.navigate(Route.Settings) }
            )
        }

        composable<Route.AddMusic> { backStackEntry ->
            val route: Route.AddMusic = backStackEntry.toRoute()
            AddMusicScreen(
                musicId = route.musicId,
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.MusicDetail> { backStackEntry ->
            val route: Route.MusicDetail = backStackEntry.toRoute()
            MusicDetailScreen(
                musicId = route.musicId,
                onBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(Route.AddMusic(id)) }
            )
        }

        composable<Route.Settings> {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
