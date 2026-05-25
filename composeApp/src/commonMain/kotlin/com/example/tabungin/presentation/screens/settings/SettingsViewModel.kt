package com.example.tabungin.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabungin.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isDarkMode: Boolean       = false,
    val notifikasiAktif: Boolean  = true,
    val namaUser: String          = ""
)

class SettingsViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        // Observe preferences from DataStore
        viewModelScope.launch {
            // Ambil data Dark Mode
            launch {
                userPreferences.isDarkMode.collect { isDark ->
                    _uiState.update { it.copy(isDarkMode = isDark) }
                }
            }
            // Ambil data Nama User dari DataStore saat aplikasi pertama dibuka
            launch {
                userPreferences.namaUser.collect { name ->
                    _uiState.update { it.copy(namaUser = name) }
                }
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            val newValue = !_uiState.value.isDarkMode
            userPreferences.setDarkMode(newValue)
            // Hapus update manual UI State di sini jika flow isDarkMode di atas sudah otomatis trigger
        }
    }

    fun toggleNotifikasi() {
        _uiState.update { it.copy(notifikasiAktif = !it.notifikasiAktif) }
        // TODO: Implement notification scheduling if needed
    }

    fun onNamaUserChange(name: String) {
        _uiState.update { it.copy(namaUser = name) }

        viewModelScope.launch {
            userPreferences.setNamaUser(name)
        }
    }
}