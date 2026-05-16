package com.example.bridgebit.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.bridgebit.presentation.screens.workspace.WorkspaceScreen
import com.example.bridgebit.presentation.screens.dashboard.DashboardScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)

    NavHost(
        navController = navController,
        startDestination = Route.Dashboard,
        modifier = modifier
    ) {
        composable<Route.Dashboard> {
            DashboardScreen(
                onNavigateToWorkspace = { navigationActions.navigateToWorkspace() },
                onNavigateToDetail = { id -> navigationActions.navigateToTranslationDetail(id) },
                onNavigateToAI = { navigationActions.navigateToAI() }
            )
        }

        composable<Route.Workspace> { backStackEntry ->
            val route: Route.Workspace = backStackEntry.toRoute()
            WorkspaceScreen(
                onNavigateBack = { navigationActions.navigateBack() }
            )
        }

        // --- Kerangka Layar Tambahan dari Target Fitur Readme ---

        composable<Route.Vault> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Phrase Vault Screen (Tempat Menyimpan Kosakata Penting)")
            }
        }

        composable<Route.Insights> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Learning Insights Screen (Statistik & Grafik Belajar Anda)")
            }
        }

        composable<Route.Settings> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Settings Screen (Pengaturan Aplikasi & Akun)")
            }
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToDashboard() {
            navController.navigate(Route.Dashboard) { popUpTo(Route.Dashboard) { inclusive = true } }
        }
        override fun navigateToWorkspace(translationId: Long?) {
            navController.navigate(Route.Workspace(translationId))
        }
        override fun navigateToTranslationDetail(translationId: Long) {
            // Ditangani di Sprint berikutnya
        }
        override fun navigateToAI(translationId: Long?, initialText: String?) {
            // Ditangani di Sprint berikutnya
        }
        override fun navigateToVault() { navController.navigate(Route.Vault) }
        override fun navigateToInsights() { navController.navigate(Route.Insights) }
        override fun navigateToSettings() { navController.navigate(Route.Settings) }
        override fun navigateBack() { navController.popBackStack() }
    }
}