package com.soundletter.app.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundletter.app.domain.repository.LetterRepository
import com.soundletter.app.domain.repository.PreferenceRepository
import com.soundletter.app.core.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: LetterRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = preferenceRepository.isDarkMode

    private val _clearHistoryStatus = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val clearHistoryStatus: StateFlow<UiState<Unit>> = _clearHistoryStatus.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) {
        preferenceRepository.toggleDarkMode(enabled)
    }

    fun clearLocalHistory() {
        viewModelScope.launch {
            _clearHistoryStatus.value = UiState.Loading
            try {
                repository.clearHistory()
                _clearHistoryStatus.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _clearHistoryStatus.value = UiState.Error(e.message ?: "Gagal menghapus riwayat")
            }
        }
    }

    fun resetStatus() {
        _clearHistoryStatus.value = UiState.Idle
    }
}
