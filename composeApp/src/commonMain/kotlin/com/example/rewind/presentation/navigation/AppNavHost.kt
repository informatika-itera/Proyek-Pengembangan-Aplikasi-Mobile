package com.example.rewind.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.rewind.presentation.screens.addmovie.AddMovieScreen
import com.example.rewind.presentation.screens.ai.AIAssistantScreen
import com.example.rewind.presentation.screens.detail.DetailScreen
import com.example.rewind.presentation.screens.home.HomeScreen
import com.example.rewind.presentation.screens.splash.SplashScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Splash
    ) {
        composable<Route.Splash> {
            SplashScreen(
                onFinished = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Home> {
            HomeScreen(
                onAddClick = { navController.navigate(Route.AddMovie()) },
                onMovieClick = { id -> navController.navigate(Route.MovieDetail(movieId = id)) },
                onAIClick = { navController.navigate(Route.AIAssistant) }
            )
        }

        composable<Route.AddMovie> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.AddMovie>()
            AddMovieScreen(
                movieId = route.movieId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Route.MovieDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.MovieDetail>()
            DetailScreen(
                movieId = route.movieId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(Route.AddMovie(movieId = id)) }
            )
        }

        composable<Route.AIAssistant> {
            AIAssistantScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}