package com.example.Feelia.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Feelia.data.local.datastore.ThemeMode
import com.example.Feelia.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        userPreferences.themeMode,
        userPreferences.showPreview,
        userPreferences.isNotificationEnabled
    ) { themeMode, showPreview, notificationEnabled ->
        SettingsUiState(
            themeMode = themeMode,
            showPreview = showPreview,
            notificationEnabled = notificationEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun onThemeModeChanged(mode: ThemeMode) {
        viewModelScope.launch { userPreferences.setThemeMode(mode) }
    }

    fun onShowPreviewChanged(show: Boolean) {
        viewModelScope.launch { userPreferences.setShowPreview(show) }
    }

    fun onNotificationEnabledChanged(enabled: Boolean) {
        viewModelScope.launch { userPreferences.setNotificationEnabled(enabled) }
    }
}

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val showPreview: Boolean = true,
    val notificationEnabled: Boolean = true
)