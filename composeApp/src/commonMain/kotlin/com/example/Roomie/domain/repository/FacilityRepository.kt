package com.example.Roomie.domain.repository

import com.example.Roomie.domain.model.Building
import com.example.Roomie.domain.model.Room
import kotlinx.coroutines.flow.Flow

interface FacilityRepository {
    fun getBuildings(): Flow<List<Building>>
    fun getRoomsByFloor(buildingId: String, floor: Int): Flow<List<Room>>
    fun getRoomsByBuilding(buildingId: String): Flow<List<Room>>
    fun getRoomById(roomId: String): Flow<Room?>
}
