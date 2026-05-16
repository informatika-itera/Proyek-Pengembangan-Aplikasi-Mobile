package com.example.bridgebit.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.bridgebit.presentation.screens.workspace.WorkspaceScreen
import com.example.bridgebit.presentation.screens.dashboard.DashboardScreen
// import com.example.bridgebit.presentation.screens.detail.TranslationDetailScreen
// import com.example.bridgebit.presentation.screens.ai.AIAssistantScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)

    NavHost(
        navController = navController,
        startDestination = Route.Dashboard, // Diubah menjadi Dashboard
        modifier = modifier
    ) {
        // ==========================================
        // 🔥 AKTIF UNTUK SPRINT 2
        // ==========================================

        composable<Route.Dashboard> {
            DashboardScreen(
                onNavigateToWorkspace = { navigationActions.navigateToWorkspace() },
                // Fitur di bawah ini dimatikan sementara agar tidak error saat diklik
                onNavigateToDetail = { /* translationId -> navigationActions.navigateToTranslationDetail(translationId) */ },
                onNavigateToAI = { /* navigationActions.navigateToAI() */ }
            )
        }

        composable<Route.Workspace> { backStackEntry ->
            val route: Route.Workspace = backStackEntry.toRoute()
            WorkspaceScreen(
                // translationId = route.translationId,
                onNavigateBack = { navigationActions.navigateBack() }
                // onNavigateToDetail = { id -> navigationActions.navigateToTranslationDetail(id) },
                // onNavigateToAI = { id, text -> navigationActions.navigateToAI(id, text) }
            )
        }

        /*
        composable<Route.TranslationDetail> { backStackEntry ->
            val route: Route.TranslationDetail = backStackEntry.toRoute()
            TranslationDetailScreen(
                translationId = route.translationId,
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateToWorkspace = { navigationActions.navigateToWorkspace(route.translationId) },
                onShare = { _ -> }
            )
        }

        composable<Route.AIAssistant> { backStackEntry ->
            val route: Route.AIAssistant = backStackEntry.toRoute()
            AIAssistantScreen(
                translationId = route.translationId,
                initialText = route.initialText,
                onNavigateBack = { navigationActions.navigateBack() },
                onApplyResult = null
            )
        }
        */
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
            // Dinonaktifkan: navController.navigate(Route.TranslationDetail(translationId))
        }

        override fun navigateToAI(translationId: Long?, initialText: String?) {
            // Dinonaktifkan: navController.navigate(Route.AIAssistant(translationId, initialText))
        }

        override fun navigateToVault() {
            // Dinonaktifkan: navController.navigate(Route.Vault)
        }

        override fun navigateToSettings() {
            // Dinonaktifkan: navController.navigate(Route.Settings)
        }

        override fun navigateToInsights() {
            // Dinonaktifkan: navController.navigate(Route.Insights)
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}