package id.my.sinanonym.mybawanggacha

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import id.my.sinanonym.mybawanggacha.domain.settings.model.AppColorScheme
import id.my.sinanonym.mybawanggacha.domain.settings.model.ThemeMode
import id.my.sinanonym.mybawanggacha.domain.settings.repository.SettingsRepository
import id.my.sinanonym.mybawanggacha.presentation.navigation.AppNavHost
import id.my.sinanonym.mybawanggacha.presentation.theme.MBGTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App(
    onDarkThemeChange: (Boolean) -> Unit = {}
) {
    KoinContext {
        val settingsRepository = koinInject<SettingsRepository>()
        val systemDarkTheme = isSystemInDarkTheme()
        val themeMode by settingsRepository.themeMode.collectAsState(initial = ThemeMode.System)
        val appColorScheme by settingsRepository.appColorScheme.collectAsState(initial = AppColorScheme.CodeGeass)
        val isDarkMode = themeMode.resolve(systemDarkTheme)

        LaunchedEffect(isDarkMode) {
            onDarkThemeChange(isDarkMode)
        }

        MBGTheme(darkTheme = isDarkMode, appColorScheme = appColorScheme) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavHost(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                )
            }
        }
    }
}
