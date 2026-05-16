package com.example.rosea.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rosea.presentation.screens.ai.AIAssistantScreen
import com.example.rosea.presentation.screens.home.HomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Routes.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToAddNote = { /* Nanti kita ubah jadi navigasi ke CartScreen */ },
                onNavigateToDetail = { id -> /* Nanti kita ubah jadi Detail Produk */ },
                onNavigateToAI = { navController.navigate(Routes.AI_ASSISTANT) }
            )
        }

        composable(Routes.AI_ASSISTANT) {
            AIAssistantScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}