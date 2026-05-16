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
import com.kelazzz.app.presentation.screens.login.LoginScreen
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

/**
 * Route khusus untuk MainScreen (wrapper dengan bottom nav).
 * Dipisah dari Route biasa karena ini bukan tab, melainkan container.
 */
@Serializable
data object MainRoute

/**
 * Root navigation host untuk KelazZz
 *
 * Hanya 2 destination di level root:
 * - Login → form login
 * - MainRoute → MainScreen (berisi bottom nav + 5 tab)
 */
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val preferences: UserPreferences = koinInject()
    val isLoggedIn by preferences.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)

    val startDestination: Any = if (isLoggedIn) MainRoute else Route.Login

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Route.Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(MainRoute) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                }
            )
        }

        composable<MainRoute> {
            MainScreen(
                onLogout = {
                    navController.navigate(Route.Login) {
                        popUpTo(MainRoute) { inclusive = true }
                    }
                }
            )
        }
    }
}
