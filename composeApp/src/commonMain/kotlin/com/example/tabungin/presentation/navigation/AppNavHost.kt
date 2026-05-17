package com.example.tabungin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tabungin.presentation.screens.add_edit.AddEditScreen
import com.example.tabungin.presentation.screens.detail.DetailScreen
import com.example.tabungin.presentation.screens.home.HomeScreen
import com.example.tabungin.presentation.screens.riwayat.RiwayatScreen
import com.example.tabungin.presentation.screens.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController    = navController,
        startDestination = Routes.Home.route
    ) {
        composable(Routes.Home.route) {
            HomeScreen(
                onNavigateToDetail  = { navController.navigate(Routes.Detail.createRoute(it)) },
                onNavigateToAddEdit = { navController.navigate(Routes.AddEdit.createRoute()) },
                onNavigateToRiwayat  = { navController.navigate(Routes.Riwayat.route) },
                onNavigateToSettings = { navController.navigate(Routes.Settings.route) }
            )
        }

        composable(
            route     = Routes.Detail.route,
            arguments = Routes.Detail.arguments
        ) { back ->
            val targetId = back.arguments?.getLong("targetId") ?: return@composable
            DetailScreen(
                targetId        = targetId,
                onNavigateBack  = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Routes.AddEdit.createRoute(it)) }
            )
        }

        composable(
            route     = Routes.AddEdit.route,
            arguments = Routes.AddEdit.arguments
        ) { back ->
            val targetId = back.arguments?.getLong("targetId")?.takeIf { it != -1L }
            AddEditScreen(
                targetId       = targetId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Riwayat.route) {
            RiwayatScreen(
                onNavigateBack      = { navController.popBackStack() },
                onNavigateToDetail  = { navController.navigate(Routes.Detail.createRoute(it)) }
            )
        }

        composable(Routes.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToHome() {
            navController.navigate(Route.Home) {
                popUpTo(Route.Home) { inclusive = true }
            }
        }
        
        override fun navigateToAddNote(noteId: Long?) {
            navController.navigate(Route.AddNote(noteId))
        }
        
        override fun navigateToNoteDetail(noteId: Long) {
            navController.navigate(Route.NoteDetail(noteId))
        }
        
        override fun navigateToAIAssistant(noteId: Long?, initialText: String?) {
            navController.navigate(Route.AIAssistant(noteId, initialText))
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}
