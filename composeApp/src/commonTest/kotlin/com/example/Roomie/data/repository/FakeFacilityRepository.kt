package com.example.Roomie.data.repository

import com.example.Roomie.domain.model.Building
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.repository.FacilityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeFacilityRepository : FacilityRepository {
    private val _buildings = MutableStateFlow<List<Building>>(emptyList())
    private val _rooms = MutableStateFlow<List<Room>>(emptyList())

    override fun getBuildings(): Flow<List<Building>> = _buildings.asStateFlow()

    override fun getRoomsByFloor(buildingId: String, floor: Int): Flow<List<Room>> {
        return _rooms.asStateFlow().map { rooms ->
            rooms.filter { it.floor == floor }
        }
    }

    override fun getRoomById(roomId: String): Flow<Room?> {
        return _rooms.asStateFlow().map { rooms ->
            rooms.find { it.id == roomId }
        }
    }
    
    fun setBuildings(buildings: List<Building>) {
        _buildings.value = buildings
    }
    
    fun setRooms(rooms: List<Room>) {
        _rooms.value = rooms
    }
}
