package com.example.Roomie.data.repository

import com.example.Roomie.domain.model.Building
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
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
            rooms.filter { it.buildingId == buildingId && it.floor == floor }
        }
    }

    override fun getRoomsByBuilding(buildingId: String): Flow<List<Room>> {
        return _rooms.asStateFlow().map { rooms ->
            rooms.filter { it.buildingId == buildingId }
        }
    }

    override fun searchRooms(query: String): Flow<List<Room>> {
        return _rooms.asStateFlow().map { rooms ->
            rooms.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    override fun getRoomById(roomId: String): Flow<Room?> {
        return _rooms.asStateFlow().map { rooms ->
            rooms.find { it.id == roomId }
        }
    }

    override suspend fun updateRoomStatus(
        roomId: String,
        status: RoomStatus,
        borrowerName: String?,
        maintenanceDescription: String?
    ) {
        val currentRooms = _rooms.value.toMutableList()
        val index = currentRooms.indexOfFirst { it.id == roomId }
        if (index != -1) {
            currentRooms[index] = currentRooms[index].copy(
                status = status,
                borrowerName = borrowerName,
                maintenanceDescription = maintenanceDescription
            )
            _rooms.value = currentRooms
        }
    }
    
    fun setBuildings(buildings: List<Building>) {
        _buildings.value = buildings
    }
    
    fun setRooms(rooms: List<Room>) {
        _rooms.value = rooms
    }
}
