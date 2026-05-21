package com.example.travelplanner.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner.domain.model.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Representasi State UI untuk Home Screen.
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val recentTrips: List<Trip> = emptyList(),
    val errorMessage: String? = null
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadRecentTrips()
    }

    /**
     * Memuat daftar perjalanan terbaru yang tersimpan di dalam database lokal.
     */
    fun loadRecentTrips() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Spesifikasi tipe secara eksplisit untuk memaksa kompilator mendeteksi dependensi Trip
                val mockTrips: List<Trip> = listOf(
                    Trip(
                        id = "1",
                        destination = "Bandung",
                        startDate = "10 Juli 2026",
                        endDate = "12 Juli 2026",
                        duration = "2 Hari",
                        vibe = "Kuliner + Santai",
                        itineraryItems = emptyList()
                    ),
                    Trip(
                        id = "2",
                        destination = "Yogyakarta",
                        startDate = "15 Agustus 2026",
                        endDate = "18 Agustus 2026",
                        duration = "3 Hari",
                        vibe = "Sejarah + Alam",
                        itineraryItems = emptyList()
                    )
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    recentTrips = mockTrips,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Gagal memuat rencana perjalanan: ${e.message}"
                )
            }
        }
    }
}