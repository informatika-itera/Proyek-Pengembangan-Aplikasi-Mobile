package com.example.rosea

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.rosea.data.local.datastore.UserPreferences
import com.example.rosea.presentation.navigation.Routes
import com.example.rosea.presentation.screens.main.MainScreen
import com.example.rosea.presentation.theme.RoseaTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    // 1. Ambil instance SyncManager secara global menggunakan Koin
    val syncManager = koinInject<com.example.rosea.domain.usecase.OrderSyncManager>()
    val userPreferences = koinInject<UserPreferences>()
    
    // Ambil preferensi dark mode
    val isDarkMode by userPreferences.isDarkMode.collectAsState(initial = false)
    
    // Cek apakah onboarding sudah selesai
    val isOnboardingCompleted by userPreferences.isOnboardingCompleted.collectAsState(initial = null)

    // 2. Nyalakan mesin pemantau di latar belakang (hanya dipanggil sekali)
    androidx.compose.runtime.LaunchedEffect(Unit) {
        syncManager.startObserving()
    }

    RoseaTheme(darkTheme = isDarkMode) {
        // Tentukan startDestination berdasarkan status onboarding
        if (isOnboardingCompleted != null) {
            val startRoute = if (isOnboardingCompleted == true) Routes.HOME else Routes.ONBOARDING
            MainScreen(startDestination = startRoute)
        }
    }
}
