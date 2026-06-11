package id.pusakakata

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import id.pusakakata.presentation.navigation.AppNavHost
import id.pusakakata.presentation.screens.settings.AppTheme
import id.pusakakata.presentation.screens.settings.SettingsViewModel
import id.pusakakata.presentation.theme.PusakaKataTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PusakaApp() {
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val theme by settingsViewModel.theme.collectAsState()
    
    val darkTheme = when (theme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }

    PusakaKataTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()
        AppNavHost(navController = navController)
    }
}
