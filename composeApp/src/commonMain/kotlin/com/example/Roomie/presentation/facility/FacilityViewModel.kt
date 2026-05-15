package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FacilityUiState {
    data object Loading : FacilityUiState
    data class Success(val rooms: List<Room>) : FacilityUiState
    data class Error(val message: String) : FacilityUiState
}

class FacilityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<FacilityUiState>(FacilityUiState.Loading)
    val uiState: StateFlow<FacilityUiState> = _uiState.asStateFlow()

    init {
        loadRooms()
    }

    private fun loadRooms() {
        viewModelScope.launch {
            _uiState.value = FacilityUiState.Loading
            delay(1000) // Simulasikan loading
            try {
                val dummyRooms = mutableListOf<Room>()
                for (row in 1..5) {
                    for (col in 1..4) {
                        val id = "R-$row$col"
                        val status = when {
                            (row + col) % 5 == 0 -> RoomStatus.MAINTENANCE
                            (row * col) % 3 == 0 -> RoomStatus.BOOKED
                            else -> RoomStatus.AVAILABLE
                        }
                        dummyRooms.add(
                            Room(
                                id = id,
                                name = "Ruang $row.$col",
                                status = status,
                                row = row,
                                col = col
                            )
                        )
                    }
                }
                _uiState.value = FacilityUiState.Success(dummyRooms)
            } catch (e: Exception) {
                _uiState.value = FacilityUiState.Error("Gagal memuat data ruangan")
            }
        }
    }
}
