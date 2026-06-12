package com.example.rewind.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rewind.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ==================== UI STATE ====================

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val sortBy: String = "UPDATED_DESC",
    val notificationsEnabled: Boolean = true,
    val isLoading: Boolean = false
)

// ==================== VIEWMODEL ====================

class SettingsViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(isLoading = true))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        // Observe semua preference secara reaktif dan gabungkan
        viewModelScope.launch {
            combine(
                userPreferences.isDarkMode,
                userPreferences.sortBy,
                userPreferences.notificationsEnabled
            ) { darkMode, sortBy, notifEnabled ->
                SettingsUiState(
                    isDarkMode = darkMode,
                    sortBy = sortBy,
                    notificationsEnabled = notifEnabled,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            userPreferences.setDarkMode(!_uiState.value.isDarkMode)
        }
    }

    fun setSortBy(sortBy: String) {
        viewModelScope.launch {
            userPreferences.setSortBy(sortBy)
        }
    }

    fun toggleNotifications() {
        viewModelScope.launch {
            userPreferences.setNotificationsEnabled(!_uiState.value.notificationsEnabled)
        }
    }
}