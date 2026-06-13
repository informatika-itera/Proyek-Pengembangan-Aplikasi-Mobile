package com.example.movein

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.movein.presentation.AppState
import com.example.movein.presentation.JourneyLog
import com.example.movein.presentation.auth.AuthViewModel
import com.example.movein.presentation.navigation.AppNavHost
import com.example.movein.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    var isLightMode by rememberSaveable { mutableStateOf(false) }
    var appState by rememberSaveable { mutableStateOf(AppState.NEUTRAL) }
    var momentum by rememberSaveable { mutableStateOf(0) }
    var logs by remember { mutableStateOf(listOf<JourneyLog>()) }

    KoinContext {
        // SUNTIKAN KUNCI: Mengambil AuthViewModel dari Koin secara otomatis
        val authViewModel = koinViewModel<AuthViewModel>()

        // Menggunakan tema utama aplikasi movein
        NoteAITheme(darkTheme = !isLightMode) {
            AppNavHost(
                authViewModel = authViewModel, // OPER KE NAV HOST: Agar LoginScreen bisa memakainya
                isLightMode = isLightMode,
                onThemeToggle = { isLightMode = it },
                appState = appState,
                onAppStateChange = { appState = it },
                momentum = momentum,
                onMomentumChange = { momentum = it },
                logs = logs,
                onLogsChange = { logs = it }
            )
        }
    }
}