package com.example.raillog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.raillog.data.local.datastore.UserPreferences
import com.example.raillog.presentation.screens.welcome.WelcomeScreen
import com.example.raillog.presentation.screens.login.LoginScreen
import com.example.raillog.presentation.screens.login.RegisterScreen
import com.example.raillog.presentation.screens.staff_main.StaffMainScreen
import com.example.raillog.presentation.screens.requisition.RequisitionScreen
import com.example.raillog.presentation.screens.requisition.RequisitionViewModel
import com.example.raillog.presentation.screens.admin_main.AdminMainScreen
import com.example.raillog.presentation.screens.admin_main.VerificationDetailScreen
import com.example.raillog.presentation.screens.ai.AIAssistantScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val coroutineScope = rememberCoroutineScope()
    val userPreferences: UserPreferences = koinInject()

    NavHost(
        navController = navController,
        startDestination = Route.Welcome,
        modifier = modifier
    ) {
        composable<Route.Welcome> {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Route.Login) {
                        popUpTo<Route.Welcome> { inclusive = true }
                    }
                },
                onAutoLogin = { role ->
                    val destination = if (role == "admin") Route.AdminMain else Route.StaffMain
                    navController.navigate(destination) {
                        popUpTo<Route.Welcome> { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Login> {
            LoginScreen(
                onLoginSuccess = { role ->
                    val destination = if (role == "admin") Route.AdminMain else Route.StaffMain
                    navController.navigate(destination) {
                        popUpTo<Route.Login> { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Route.Register)
                }
            )
        }

        composable<Route.Register> {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Route.StaffMain> {
            StaffMainScreen(
                onNavigateToNewRequisition = {
                    navController.navigate(Route.RequisitionWizard(draftId = null))
                },
                onNavigateToResumeDraft = { draftId ->
                    navController.navigate(Route.RequisitionWizard(draftId = draftId))
                },
                onNavigateToAIAssistant = {
                    navController.navigate(Route.ContextualAI)
                },
                onLogout = {
                    coroutineScope.launch {
                        try {
                            userPreferences.clearUserSession()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    navController.navigate(Route.Login) {
                        popUpTo<Route.StaffMain> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Route.RequisitionWizard> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.RequisitionWizard>()
            val viewModel: RequisitionViewModel = koinViewModel()
            RequisitionScreen(
                viewModel = viewModel,
                draftId = route.draftId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Route.StaffMain) {
                        popUpTo<Route.StaffMain> { inclusive = true }
                    }
                }
            )
        }

        composable<Route.AdminMain> {
            AdminMainScreen(
                onNavigateToVerificationDetail = { reqId ->
                    navController.navigate(Route.VerificationDetail(reqId))
                },
                onNavigateToAIAssistant = {
                    navController.navigate(Route.ContextualAI)
                },
                onLogout = {
                    coroutineScope.launch {
                        try {
                            userPreferences.clearUserSession()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    navController.navigate(Route.Login) {
                        popUpTo<Route.AdminMain> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Route.VerificationDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.VerificationDetail>()
            VerificationDetailScreen(
                requisitionId = route.requisitionId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Route.ContextualAI> {
            AIAssistantScreen(
                noteId = null,
                initialText = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}