package com.studyhub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studyhub.presentation.navigation.AppNavHost
import com.studyhub.presentation.theme.StudyHubTheme
import com.studyhub.presentation.theme.ThemeViewModel
import com.studyhub.core.util.SystemAppearance
import com.studyhub.data.sync.SyncManager
import com.studyhub.presentation.components.LocalReduceMotion
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    openScreen: String? = null,
    taskId: String? = null,
    onScreenOpened: () -> Unit = {}
) {
    KoinContext {
        val themeViewModel: ThemeViewModel = koinViewModel()
        val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
        
        val syncManager: SyncManager = koinInject()

        androidx.compose.runtime.LaunchedEffect(Unit) {
            syncManager.startAutoSync()
        }

        SystemAppearance(isDarkMode = isDarkMode)
        
        // Provide LocalReduceMotion based on system settings if possible,
        // or just default to false for now as a placeholder for accessibility settings
        CompositionLocalProvider(LocalReduceMotion provides false) {
            StudyHubTheme(darkTheme = isDarkMode) {
                AppNavHost(
                    openScreen = openScreen,
                    taskId = taskId,
                    onScreenOpened = onScreenOpened
                )
            }
        }
    }
}
