package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.repository.FacilityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

sealed interface FacilityUiState {
    data object Loading : FacilityUiState
    data class Success(
        val rooms: List<Room>,
        val selectedFloor: Int = 1
    ) : FacilityUiState {
        val filteredRooms: List<Room> get() = rooms.filter { it.floor == selectedFloor }
    }
    data class Error(val message: String) : FacilityUiState
}

class FacilityViewModel(
    private val facilityRepository: FacilityRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<FacilityUiState>(FacilityUiState.Loading)
    val uiState: StateFlow<FacilityUiState> = _uiState.asStateFlow()

    init {
        observeRooms()
    }

    fun selectFloor(floor: Int) {
        val currentState = _uiState.value
        if (currentState is FacilityUiState.Success) {
            _uiState.update { currentState.copy(selectedFloor = floor) }
        }
    }

    private fun observeRooms() {
        // Gabungkan flow dari semua lantai GKU2
        combine(
            facilityRepository.getRoomsByFloor("GKU2", 1),
            facilityRepository.getRoomsByFloor("GKU2", 2),
            facilityRepository.getRoomsByFloor("GKU2", 3),
            facilityRepository.getRoomsByFloor("GKU2", 4)
        ) { r1, r2, r3, r4 ->
            r1 + r2 + r3 + r4
        }.onEach { allRooms ->
            _uiState.value = FacilityUiState.Success(allRooms)
        }.launchIn(viewModelScope)
    }
}
