package com.example.Roomie.data.repository

import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.domain.model.Building
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.model.RoomType
import com.example.Roomie.domain.repository.FacilityRepository
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FacilityRepositoryImpl(
    database: RoomieDatabase
) : FacilityRepository {
    private val queries = database.facilityQueries

    override fun getBuildings(): Flow<List<Building>> {
        return queries.getAllBuildings()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Building(
                        id = entity.id,
                        name = entity.name,
                        description = entity.description,
                        isAvailable = entity.isAvailable == 1L
                    )
                }
            }
    }

    override fun getRoomsByFloor(buildingId: String, floor: Int): Flow<List<Room>> {
        return queries.getRoomsByFloor(buildingId, floor.toLong())
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override fun getRoomsByBuilding(buildingId: String): Flow<List<Room>> {
        return queries.getRoomsByBuilding(buildingId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override fun searchRooms(query: String): Flow<List<Room>> {
        return queries.searchRooms(query)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override fun getRoomById(roomId: String): Flow<Room?> {
        return queries.getRoomById(roomId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.toDomain()
            }
    }

    private fun com.example.Roomie.data.local.RoomEntity.toDomain(): Room {
        return Room(
            id = id,
            name = name,
            floor = floor.toInt(),
            status = RoomStatus.valueOf(status),
            type = RoomType.valueOf(type),
            capacity = capacity.toInt(),
            hasAc = hasAc == 1L,
            hasProjector = hasProjector == 1L,
            borrowerName = borrowerName,
            maintenanceDescription = maintenanceDescription
        )
    }

    suspend fun seedData() {
        withContext(Dispatchers.IO) {
            val existing = queries.getAllBuildings().executeAsList()
            if (existing.isEmpty()) {
                // Seed Buildings
                queries.insertBuilding("GKU1", "Gedung Kuliah Umum 1", "Pusat kegiatan akademik utama", 0L)
                queries.insertBuilding("GKU2", "Gedung Kuliah Umum 2", "Gedung kuliah terpadu dengan fasilitas modern", 1L)
                queries.insertBuilding("GEDUNG-E", "Gedung E", "Laboratorium dan kantor jurusan", 0L)
                queries.insertBuilding("GEDUNG-F", "Gedung F", "Ruang kelas dan pusat penelitian", 0L)

                // Seed GKU2 Rooms
                for (f in 1..3) {
                    for (i in 1..25) {
                        val roomNum = f * 100 + i
                        val status = when {
                            roomNum % 7 == 0 -> RoomStatus.MAINTENANCE
                            roomNum % 3 == 0 -> RoomStatus.BOOKED
                            else -> RoomStatus.AVAILABLE
                        }
                        
                        queries.insertRoom(
                            id = "GKU2-$roomNum",
                            buildingId = "GKU2",
                            name = roomNum.toString(),
                            floor = f.toLong(),
                            status = status.name,
                            type = RoomType.REGULAR.name,
                            capacity = 40L,
                            hasAc = 1L,
                            hasProjector = 1L,
                            borrowerName = if (status == RoomStatus.BOOKED) "Mata Kuliah PAM - Dosen X" else null,
                            maintenanceDescription = if (status == RoomStatus.MAINTENANCE) "Perbaikan AC Sentral" else null
                        )
                    }
                }
                for (i in 1..20) {
                    val roomNum = 400 + i
                    queries.insertRoom(
                        id = "GKU2-$roomNum",
                        buildingId = "GKU2",
                        name = roomNum.toString(),
                        floor = 4L,
                        status = RoomStatus.AVAILABLE.name,
                        type = RoomType.REGULAR.name,
                        capacity = 40L,
                        hasAc = 1L,
                        hasProjector = 1L,
                        borrowerName = null,
                        maintenanceDescription = null
                    )
                }
                queries.insertRoom(
                    id = "GKU2-AULA",
                    buildingId = "GKU2",
                    name = "Aula GKU 2",
                    floor = 4L,
                    status = RoomStatus.AVAILABLE.name,
                    type = RoomType.AULA.name,
                    capacity = 300L,
                    hasAc = 1L,
                    hasProjector = 1L,
                    borrowerName = null,
                    maintenanceDescription = null
                )
            }
        }
    }
}
