package com.example.neurodeck

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.neurodeck.presentation.navigation.AppNavHost
import com.example.neurodeck.presentation.theme.neurodeckTheme
import org.koin.compose.KoinContext

/**
 * Entry point composable aplikasi NeuroDeck.
 *
 * Wrap urutan (dari luar ke dalam):
 *   1. KoinContext      — Provide Koin-injected dependencies ke composables
 *                         (via koinInject() dan koinViewModel() di child composables).
 *   2. neurodeckTheme   — Material 3 theming (Light/Dark adaptif system).
 *                         Custom color scheme dari presentation/theme/Theme.kt.
 *   3. AppNavHost       — Root navigation host dengan Scaffold (TopBar +
 *                         BottomNav) + ModalNavigationDrawer.
 *                         Auto-decide chrome visibility per route.
 *
 * Start destination: Screen.Home (default di AppNavHost) — sesuai 5-tab
 * pattern Material 3 di mana Home jadi landing tab.
 */
@Composable
fun App() {
    KoinContext {
        neurodeckTheme {
            val navController = rememberNavController()
            AppNavHost(navController = navController)
        }
    }
}