package com.example.travelplanner.presentation.screens.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.repository.TripRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MyTripsUiState(
    val isLoading: Boolean = false,
    val trips: List<Trip> = emptyList(),
    val errorMessage: String? = null
)

class MyTripsViewModel(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MyTripsUiState>(MyTripsUiState())
    val uiState: StateFlow<MyTripsUiState> = _uiState.asStateFlow()

    init {
        loadTrips()
    }

    fun loadTrips() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            tripRepository.getAllTrips()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat rencana perjalanan: ${e.message}"
                    )
                }
                .collect { trips ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        trips = trips,
                        errorMessage = null
                    )
                }
        }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            try {
                tripRepository.deleteTrip(tripId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Gagal menghapus perjalanan: ${e.message}")
            }
        }
    }
}
