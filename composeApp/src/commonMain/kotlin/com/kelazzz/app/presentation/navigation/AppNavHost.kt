package com.kelazzz.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kelazzz.app.data.local.datastore.UserPreferences
import com.kelazzz.app.presentation.screens.home.HomeScreen
import com.kelazzz.app.presentation.screens.login.LoginScreen
import org.koin.compose.koinInject

/**
 * Main navigation host untuk KelazZz
 *
 * Start destination ditentukan berdasarkan login state:
 * - Sudah login → Home
 * - Belum login → Login
 */
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val preferences: UserPreferences = koinInject()
    val isLoggedIn by preferences.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)

    val startDestination: Route = if (isLoggedIn) Route.Home else Route.Login

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Route.Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Home> {
            HomeScreen()
        }

        // TODO: Sprint 2 — more screens
        // composable<Route.DaftarPresensi> { DaftarPresensiScreen(...) }
        // composable<Route.Presensi> { PresensiScreen(...) }

        // TODO: Sprint 3+
        // composable<Route.Kalender> { KalenderScreen(...) }
        // composable<Route.AIAsisten> { AIAsistenScreen(...) }
        // composable<Route.Notifikasi> { NotifikasiScreen(...) }
    }
}
