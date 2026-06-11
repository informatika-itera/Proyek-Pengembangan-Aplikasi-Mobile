package com.example.sholatyuk

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.sholatyuk.presentation.navigation.AppNavHost
import com.example.sholatyuk.presentation.screens.profile.ProfileViewModel
import com.example.sholatyuk.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinContext {
        val profileViewModel: ProfileViewModel = koinViewModel()
        val isLightModeEnabled by profileViewModel.isLightModeEnabled.collectAsState()

        NoteAITheme(darkTheme = !isLightModeEnabled) {
            AppNavHost()
        }
    }
}
