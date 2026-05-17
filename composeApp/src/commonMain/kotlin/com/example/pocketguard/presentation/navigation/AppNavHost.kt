package com.example.pocketguard.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.pocketguard.presentation.screens.add_transaction.AddTransactionScreen
import com.example.pocketguard.presentation.screens.ai.AIAssistantScreen
import com.example.pocketguard.presentation.screens.detail.TransactionDetailScreen
import com.example.pocketguard.presentation.screens.home.HomeScreen
import com.example.pocketguard.presentation.screens.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)

    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomeScreen(
                onNavigateToAdd = { navigationActions.navigateToAddTransaction() },
                onNavigateToDetail = { id -> navigationActions.navigateToTransactionDetail(id) },
                onNavigateToAI = { navigationActions.navigateToAIAssistant() },
                onNavigateToSettings = { navigationActions.navigateToSettings() }
            )
        }

        composable<Route.AddTransaction> { backStackEntry ->
            val route: Route.AddTransaction = backStackEntry.toRoute()
            AddTransactionScreen(
                transactionId = route.transactionId, // Tambahkan ini agar tidak error
                onNavigateBack = { navigationActions.navigateBack() }
            )
        }

        composable<Route.TransactionDetail> { backStackEntry ->
            val route: Route.TransactionDetail = backStackEntry.toRoute()
            TransactionDetailScreen(
                transactionId = route.transactionId, // Tambahkan ini agar tidak error
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateToEdit = { id -> navigationActions.navigateToAddTransaction(id) }
            )
        }

        composable<Route.AIAssistant> { backStackEntry ->
            val route: Route.AIAssistant = backStackEntry.toRoute()
            AIAssistantScreen(
                initialText = route.initialText,
                onNavigateBack = { navigationActions.navigateBack() }
            )
        }
        composable<Route.Settings> {
            SettingsScreen(
                onNavigateBack = { navigationActions.navigateBack() }
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

        override fun navigateToAddTransaction(transactionId: Long?) {
            navController.navigate(Route.AddTransaction(transactionId))
        }

        override fun navigateToTransactionDetail(transactionId: Long) {
            // Pastikan transactionId dikirim ke constructor Route
            navController.navigate(Route.TransactionDetail(transactionId))
        }

        override fun navigateToAIAssistant(initialText: String?) {
            navController.navigate(Route.AIAssistant(initialText))
        }

        override fun navigateBack() {
            navController.popBackStack()
        }

        override fun navigateToSettings() {
            navController.navigate(Route.Settings)
        }
        }

    }
