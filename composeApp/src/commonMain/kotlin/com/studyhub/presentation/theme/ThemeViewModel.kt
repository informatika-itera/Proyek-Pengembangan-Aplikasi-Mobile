package com.studyhub.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.domain.usecase.preferences.GetDarkModeUseCase
import com.studyhub.domain.usecase.preferences.SetDarkModeUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ThemeUiState(
    val isDarkMode: Boolean = false,
    val isLoaded: Boolean = false
)

class ThemeViewModel(
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ThemeUiState())
    val uiState: StateFlow<ThemeUiState> = _uiState.asStateFlow()

    // Expose langsung sebagai StateFlow untuk App.kt
    val isDarkMode: StateFlow<Boolean> =
        getDarkModeUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false
            )

    init {
        observeDarkMode()
    }

    private fun observeDarkMode() {
        viewModelScope.launch {
            getDarkModeUseCase().collect { isDark ->
                _uiState.update {
                    it.copy(isDarkMode = isDark, isLoaded = true)
                }
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            setDarkModeUseCase(!_uiState.value.isDarkMode)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            setDarkModeUseCase(enabled)
        }
    }
}
