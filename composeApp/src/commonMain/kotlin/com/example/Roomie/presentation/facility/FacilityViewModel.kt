package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.usecase.GetRoomsByBuildingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    private val getRoomsByBuildingUseCase: GetRoomsByBuildingUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<FacilityUiState>(FacilityUiState.Loading)
    val uiState: StateFlow<FacilityUiState> = _uiState.asStateFlow()

    init {
        observeGKU2Rooms()
    }

    private fun observeGKU2Rooms() {
        viewModelScope.launch {
            _uiState.value = FacilityUiState.Loading
            getRoomsByBuildingUseCase("GKU2")
                .catch { e -> _uiState.value = FacilityUiState.Error(e.message ?: "Error") }
                .collectLatest { allRooms ->
                    if (allRooms.isNotEmpty()) {
                        val currentFloor = (uiState.value as? FacilityUiState.Success)?.selectedFloor ?: 1
                        _uiState.value = FacilityUiState.Success(allRooms, currentFloor)
                    }
                }
        }
    }

    fun selectFloor(floor: Int) {
        val currentState = _uiState.value
        if (currentState is FacilityUiState.Success) {
            _uiState.update { currentState.copy(selectedFloor = floor) }
        }
    }
}
