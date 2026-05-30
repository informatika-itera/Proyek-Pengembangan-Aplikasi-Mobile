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
    val userName: String = "",
    val userBio: String = "",
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
                userPreferences.userName,
                userPreferences.userBio
            ) { darkMode, sortBy, name, bio ->
                SettingsUiState(
                    isDarkMode = darkMode,
                    sortBy = sortBy,
                    userName = name,
                    userBio = bio,
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

    fun setUserName(name: String) {
        viewModelScope.launch {
            userPreferences.setUserName(name)
        }
    }

    fun setUserBio(bio: String) {
        viewModelScope.launch {
            userPreferences.setUserBio(bio)
        }
    }
}