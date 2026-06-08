package com.example.nutriscan.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * Compatibility wrapper.
 *
 * Root navigation sekarang sudah diatur dari App.kt:
 * - LoginScreen
 * - OnboardingScreen
 * - UserApp
 * - NutritionistApp
 *
 * File lama AppNavHost masih ikut dikompilasi, jadi dibuat sederhana agar tidak
 * bentrok dengan Routes.kt yang sekarang sudah tidak punya Route.Onboarding.
 */
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    UserApp(navController = navController)
}