package com.example.fitkos.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitkos.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        userPreferences.isDarkMode,
        userPreferences.userName,
        userPreferences.waterTarget
    ) { isDarkMode, userName, waterTarget ->
        SettingsUiState(
            isDarkMode = isDarkMode,
            userName = userName,
            waterTarget = waterTarget
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDarkMode(enabled)
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            userPreferences.setUserName(name)
        }
    }

    fun updateWaterTarget(target: Int) {
        viewModelScope.launch {
            userPreferences.setWaterTarget(target)
        }
    }
}

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val userName: String = "Sobat Kos",
    val waterTarget: Int = 8
)
