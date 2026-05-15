package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.model.RoomType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

class FacilityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<FacilityUiState>(FacilityUiState.Loading)
    val uiState: StateFlow<FacilityUiState> = _uiState.asStateFlow()

    init {
        loadGKU2Data()
    }

    fun selectFloor(floor: Int) {
        val currentState = _uiState.value
        if (currentState is FacilityUiState.Success) {
            _uiState.update { currentState.copy(selectedFloor = floor) }
        }
    }

    private fun loadGKU2Data() {
        viewModelScope.launch {
            _uiState.value = FacilityUiState.Loading
            delay(800)
            try {
                val allRooms = mutableListOf<Room>()
                
                // Lantai 1 - 3 (101-125, 201-225, 301-325)
                for (floor in 1..3) {
                    for (i in 1..25) {
                        val roomNum = floor * 100 + i
                        allRooms.add(createDummyRoom(roomNum.toString(), floor))
                    }
                }
                
                // Lantai 4 (401-420 + Aula)
                for (i in 1..20) {
                    val roomNum = 400 + i
                    allRooms.add(createDummyRoom(roomNum.toString(), 4))
                }
                allRooms.add(
                    Room(
                        id = "AULA-GKU2",
                        name = "Aula GKU 2",
                        floor = 4,
                        status = RoomStatus.AVAILABLE,
                        type = RoomType.AULA,
                        capacity = 300
                    )
                )
                
                _uiState.value = FacilityUiState.Success(allRooms)
            } catch (e: Exception) {
                _uiState.value = FacilityUiState.Error("Gagal memuat data GKU 2")
            }
        }
    }

    private fun createDummyRoom(name: String, floor: Int): Room {
        val status = when {
            name.toInt() % 7 == 0 -> RoomStatus.MAINTENANCE
            name.toInt() % 3 == 0 -> RoomStatus.BOOKED
            else -> RoomStatus.AVAILABLE
        }
        return Room(
            id = "GKU2-$name",
            name = name,
            floor = floor,
            status = status
        )
    }
}
