package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.repository.FacilityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        viewModelScope.launch {
            _uiState.value = FacilityUiState.Loading
            // Default ke GKU2 untuk sekarang sesuai request sebelumnya
            facilityRepository.getRoomsByFloor("GKU2", 1).collectLatest {
                // Repository kita sekarang sudah reactive (Flow)
                // Tapi untuk filter lantai kita handle di state Success agar UI tetap smooth saat ganti tab
            }
            
            // Re-implement observe logic to use full room list if needed or specialized flow
            // For now, let's observe all GKU2 rooms and handle floor filtering in UI State
            facilityRepository.getRoomsByFloor("GKU2", 1).collectLatest { roomsLvl1 ->
                facilityRepository.getRoomsByFloor("GKU2", 2).collectLatest { roomsLvl2 ->
                    facilityRepository.getRoomsByFloor("GKU2", 3).collectLatest { roomsLvl3 ->
                        facilityRepository.getRoomsByFloor("GKU2", 4).collectLatest { roomsLvl4 ->
                            val allGKU2Rooms = roomsLvl1 + roomsLvl2 + roomsLvl3 + roomsLvl4
                            _uiState.value = FacilityUiState.Success(allGKU2Rooms)
                        }
                    }
                }
            }
        }
    }
    
    // Simplification: In a real app, Repository should have a method to get all rooms of a building
    // For Sprint 1 refinement, we use the existing interface
}
