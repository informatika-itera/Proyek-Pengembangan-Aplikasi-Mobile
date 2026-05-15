package com.example.Roomie.domain.usecase

import com.example.Roomie.domain.model.Building
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.repository.FacilityRepository
import kotlinx.coroutines.flow.Flow

class GetBuildingsUseCase(private val repository: FacilityRepository) {
    operator fun invoke(): Flow<List<Building>> = repository.getBuildings()
}

class GetRoomsByBuildingUseCase(private val repository: FacilityRepository) {
    operator fun invoke(buildingId: String): Flow<List<Room>> = repository.getRoomsByBuilding(buildingId)
}

class SearchRoomsUseCase(private val repository: FacilityRepository) {
    operator fun invoke(query: String): Flow<List<Room>> = repository.searchRooms(query)
}

class GetRoomByIdUseCase(private val repository: FacilityRepository) {
    operator fun invoke(roomId: String): Flow<Room?> = repository.getRoomById(roomId)
}
