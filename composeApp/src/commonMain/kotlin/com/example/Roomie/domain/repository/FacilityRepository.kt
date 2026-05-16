package com.example.Roomie.domain.repository

import com.example.Roomie.domain.model.Building
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import kotlinx.coroutines.flow.Flow

interface FacilityRepository {
    fun getBuildings(): Flow<List<Building>>
    fun getRoomsByFloor(buildingId: String, floor: Int): Flow<List<Room>>
    fun getRoomsByBuilding(buildingId: String): Flow<List<Room>>
    fun searchRooms(query: String): Flow<List<Room>>
    fun getRoomById(roomId: String): Flow<Room?>
    suspend fun updateRoomStatus(
        roomId: String, 
        status: RoomStatus, 
        borrowerName: String? = null, 
        maintenanceDescription: String? = null
    )
}
