package com.example.rosea.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rosea.presentation.screens.ai.AIAssistantScreen
import com.example.rosea.presentation.screens.cart.CartScreen
import com.example.rosea.presentation.screens.detail.DetailScreen
import com.example.rosea.presentation.screens.home.HomeScreen
import com.example.rosea.presentation.screens.onboarding.OnboardingScreen
import com.example.rosea.presentation.screens.profile.*

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Routes.HOME,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToDetail = { productId ->
                    navController.navigate(Routes.createDetailRoute(productId))
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
            DetailScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCart = { navController.navigate(Routes.CART) }
            )
        }

        composable(Routes.CART) {
            CartScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.AI_ASSISTANT) {
            AIAssistantScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCart = { navController.navigate(Routes.CART) },
                onNavigateToEditProfile = { navController.navigate(Routes.EDIT_PROFILE) },
                onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                onNavigateToVouchers = { navController.navigate(Routes.VOUCHERS) },
                onNavigateToHistory = { navController.navigate(Routes.HISTORY) },
                onNavigateToAddress = { navController.navigate(Routes.ADDRESS_MANAGEMENT) },
                onNavigateToHelp = { navController.navigate(Routes.HELP_SUPPORT) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.VOUCHERS) {
            VoucherScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADDRESS_MANAGEMENT) {
            AddressManagementScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HELP_SUPPORT) {
            HelpSupportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAccountInfo = { navController.navigate(Routes.EDIT_PROFILE) },
                onNavigateToSecurity = { navController.navigate(Routes.SECURITY) },
                onNavigateToAbout = { navController.navigate(Routes.ABOUT) },
                onNavigateToTerms = { navController.navigate(Routes.TERMS) }
            )
        }

        composable(Routes.SECURITY) {
            SecurityScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.TERMS) {
            TermsOfServiceScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
