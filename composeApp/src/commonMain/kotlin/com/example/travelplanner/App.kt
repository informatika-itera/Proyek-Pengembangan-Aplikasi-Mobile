package com.example.travelplanner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.travelplanner.core.util.LocalStrings
import com.example.travelplanner.core.util.NetworkMonitorInterface
import com.example.travelplanner.core.util.StringsEN
import com.example.travelplanner.core.util.StringsID
import com.example.travelplanner.presentation.components.ConnectivityBanner
import com.example.travelplanner.presentation.theme.TravelPlannerTheme
import com.example.travelplanner.presentation.navigation.AppNavHost
import com.example.travelplanner.presentation.navigation.Route
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.compose.KoinContext

@Composable
fun App(
    networkMonitor: NetworkMonitorInterface? = null,
    initialIsEnglish: Boolean = false,
    onLanguageChange: ((Boolean) -> Unit)? = null
) {
    var isDarkMode by remember { mutableStateOf(false) }
    var isEnglish  by remember { mutableStateOf(initialIsEnglish) }
    val strings    = if (isEnglish) StringsEN else StringsID

    // Collect connectivity state — default to true (online) if no monitor
    val isOnline by (networkMonitor?.isOnline ?: flowOf(true))
        .collectAsState(initial = true)

    KoinContext {
        CompositionLocalProvider(LocalStrings provides strings) {
            TravelPlannerTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route

                val showBottomBar = when (currentRoute?.substringAfterLast(".")) {
                    "Home", "MyTrips", "Expenses", "Profile" -> true
                    else -> false
                }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(navController = navController, currentRoute = currentRoute)
                        }
                    },
                    contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
                ) { paddingValues ->
                    Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                        // ── Offline banner (slides from top) ───────────────
                        ConnectivityBanner(isOnline = isOnline)

                        // ── Navigation host ────────────────────────────────
                        Box(modifier = Modifier.weight(1f)) {
                            AppNavHost(
                                isDarkMode       = isDarkMode,
                                onToggleDarkMode = { isDarkMode = !isDarkMode },
                                isEnglish        = isEnglish,
                                onToggleLanguage = {
                                    isEnglish = !isEnglish
                                    onLanguageChange?.invoke(isEnglish)
                                },
                                navController    = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, currentRoute: String?) {
    val s = LocalStrings.current
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val tabs = listOf(
            Triple(s.navHome,    Route.Home,            Icons.Default.Home),
            Triple(s.navTrips,   Route.MyTrips,         Icons.Default.Map),
            Triple(s.navFinance, Route.Expenses(null),  Icons.Default.AccountBalanceWallet),
            Triple(s.navProfile, Route.Profile,         Icons.Default.Person)
        )

        tabs.forEach { (label, route, icon) ->
            val routeClassName = route::class.simpleName ?: ""
            val isSelected = currentRoute?.substringAfterLast(".")
                ?.substringBefore("?") == routeClassName

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(route) {
                            popUpTo(Route.Home) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp)) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = MaterialTheme.colorScheme.primary,
                    selectedTextColor   = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                    indicatorColor      = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                )
            )
        }
    }
}
