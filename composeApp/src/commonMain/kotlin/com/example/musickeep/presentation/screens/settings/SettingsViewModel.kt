package com.example.musickeep.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musickeep.data.local.datastore.UserPreferences
import com.example.musickeep.domain.repository.MusicRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val userName: String = "",
    val isDarkMode: Boolean = true,
    val totalSongs: Long = 0,
    val favoriteGenre: String = "-"
)

class SettingsViewModel(
    private val userPreferences: UserPreferences,
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        // Collect dari DataStore dan Repository secara bersamaan
        combine(
            userPreferences.userName,
            userPreferences.isDarkMode,
            repository.getTotalCount(),
            repository.getMostCommonGenre()
        ) { name, dark, count, genre ->
            SettingsUiState(
                userName = name,
                isDarkMode = dark,
                totalSongs = count,
                favoriteGenre = genre ?: "-"
            )
        }.onEach { newState ->
            _uiState.value = newState
        }.launchIn(viewModelScope)
    }

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
}
