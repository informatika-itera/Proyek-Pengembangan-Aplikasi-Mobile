package com.kelazzz.app.presentation.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kelazzz.app.data.local.datastore.UserPreferences
import com.kelazzz.app.presentation.screens.login.LoginScreen
import kelazzz.composeapp.generated.resources.Res
import kelazzz.composeapp.generated.resources.logo
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
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
    val isLoggedIn by preferences.isLoggedIn.collectAsStateWithLifecycle(initialValue = null)

    if (isLoggedIn == null) {
        // Tampilan Splash / Loading Screen premium saat DataStore sedang membaca sesi
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                            MaterialTheme.colorScheme.background
                        ),
                        startY = 0f,
                        endY = 1000f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "Logo KelazZz",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(22.dp))
                )
                Spacer(modifier = Modifier.height(28.dp))
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    } else {
        val startDestination: Any = if (isLoggedIn == true) MainRoute else Route.Login

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
}

