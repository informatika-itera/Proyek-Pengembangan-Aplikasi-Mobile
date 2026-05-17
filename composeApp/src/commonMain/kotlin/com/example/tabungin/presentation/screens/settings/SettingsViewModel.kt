package com.example.tabungin.presentation.screens.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*

data class SettingsUiState(
    val isDarkMode: Boolean       = false,
    val notifikasiAktif: Boolean  = true,
    val namaUser: String          = ""
)

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleDarkMode()             = _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    fun toggleNotifikasi()           = _uiState.update { it.copy(notifikasiAktif = !it.notifikasiAktif) }
    fun onNamaUserChange(v: String)  = _uiState.update { it.copy(namaUser = v) }
}