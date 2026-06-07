package com.example.noteai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.noteai.presentation.AppState
import com.example.noteai.presentation.JourneyLog
import com.example.noteai.presentation.navigation.AppNavHost
import com.example.noteai.presentation.theme.NoteAITheme
import com.movein.di.networkModule
import com.movein.data.repository.ActivityRepositoryImpl
import com.movein.domain.repository.ActivityRepository
import org.koin.compose.KoinContext
import org.koin.dsl.module

// Modul Koin tambahan untuk menampung data layer baru buatan Raisya
val sprint3DataModule = module {
    // Mendaftarkan ActivityRepository agar siap di-inject ke ViewModel nanti
    single<ActivityRepository> { ActivityRepositoryImpl(get()) }
}

@Composable
fun App() {
    var isLightMode by rememberSaveable { mutableStateOf(false) }
    var appState by rememberSaveable { mutableStateOf(AppState.NEUTRAL) }
    var momentum by rememberSaveable { mutableStateOf(0) }
    var logs by remember { mutableStateOf(listOf<JourneyLog>()) }

    KoinContext {
        NoteAITheme(darkTheme = !isLightMode) {
            AppNavHost(
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
