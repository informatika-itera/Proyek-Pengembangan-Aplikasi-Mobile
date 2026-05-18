package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.usecase.GetRoomByIdUseCase
import com.example.Roomie.domain.usecase.UpdateRoomStatusUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FacilityDetailViewModel(
    private val getRoomByIdUseCase: GetRoomByIdUseCase,
    private val updateRoomStatusUseCase: UpdateRoomStatusUseCase
) : ViewModel() {
    
    fun getRoom(roomId: String): StateFlow<Room?> = getRoomByIdUseCase(roomId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun updateStatus(roomId: String, status: RoomStatus, note: String?) {
        viewModelScope.launch {
            updateRoomStatusUseCase(
                roomId = roomId,
                status = status,
                borrowerName = if (status == RoomStatus.BOOKED) note else null,
                maintenanceDescription = if (status == RoomStatus.MAINTENANCE) note else null
            )
        }
    }
}
