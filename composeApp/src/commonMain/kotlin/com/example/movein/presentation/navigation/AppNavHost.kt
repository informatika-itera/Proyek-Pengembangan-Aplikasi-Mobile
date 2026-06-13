package com.example.movein.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movein.presentation.AppState
import com.example.movein.presentation.JourneyLog
import com.example.movein.presentation.auth.AuthViewModel
import com.example.movein.presentation.auth.LoginScreen
import com.example.movein.presentation.auth.RegisterScreen
import com.example.movein.presentation.screens.MainScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
}

@Composable
fun AppNavHost(
    authViewModel: AuthViewModel, // SUNTIKAN 1: Menerima AuthViewModel dari App.kt
    navController: NavHostController = rememberNavController(),
    isLightMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    appState: AppState,
    onAppStateChange: (AppState) -> Unit,
    momentum: Int,
    onMomentumChange: (Int) -> Unit,
    logs: List<JourneyLog>,
    onLogsChange: (List<JourneyLog>) -> Unit
) {
    var currentUserName by rememberSaveable { mutableStateOf("Mahasiswa") }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel, // SUNTIKAN 2: Mengoper ViewModel ke LoginScreen
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = { userName ->
                    currentUserName = userName.ifBlank { "Mahasiswa" }

                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = { userName ->
                    currentUserName = userName.ifBlank { "Mahasiswa" }

                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                userName = currentUserName,
                isLightMode = isLightMode,
                onThemeToggle = onThemeToggle,
                mentalState = appState,
                onMentalStateChange = onAppStateChange,
                momentum = momentum,
                onMomentumChange = onMomentumChange,
                logs = logs,
                onLogsChange = onLogsChange,
                onLogout = {
                    currentUserName = "Mahasiswa"

                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}