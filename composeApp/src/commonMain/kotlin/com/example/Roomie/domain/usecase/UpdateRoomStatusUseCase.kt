package com.example.Roomie.domain.usecase

import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.repository.FacilityRepository

class UpdateRoomStatusUseCase(private val repository: FacilityRepository) {
    suspend operator fun invoke(
        roomId: String,
        status: RoomStatus,
        borrowerName: String? = null,
        maintenanceDescription: String? = null
    ) = repository.updateRoomStatus(roomId, status, borrowerName, maintenanceDescription)
}
