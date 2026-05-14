package com.example.Roomie.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val summary: String) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // Simulate data fetch
                kotlinx.coroutines.delay(1000)
                _uiState.value = HomeUiState.Success("Selamat Datang di Roomie! Tidak ada laporan tertunda.")
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }
}
