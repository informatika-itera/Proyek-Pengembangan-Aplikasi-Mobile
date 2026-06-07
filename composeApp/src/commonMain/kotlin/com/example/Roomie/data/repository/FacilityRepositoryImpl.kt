package com.example.Roomie.data.repository

import com.example.Roomie.core.network.NetworkMonitor
import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.data.local.RoomEntity
import com.example.Roomie.domain.model.Building
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.model.RoomType
import com.example.Roomie.domain.repository.FacilityRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class BuildingRemoteDto(
    val id: String,
    val name: String,
    val description: String,
    val is_available: Boolean
)

@Serializable
data class RoomRemoteDto(
    val id: String,
    val building_id: String,
    val name: String,
    val floor: Int,
    val status: String,
    val capacity: Int,
    val has_ac: Boolean,
    val has_projector: Boolean
)

class FacilityRepositoryImpl(
    database: RoomieDatabase,
    private val supabaseClient: SupabaseClient,
    private val networkMonitor: NetworkMonitor,
    private val scope: CoroutineScope
) : FacilityRepository {
    private val queries = database.facilityQueries

    init {
        observeSync()
    }

    private fun observeSync() {
        scope.launch {
            networkMonitor.isOnline.collect { online ->
                if (online) {
                    try {
                        // 1. Sync Buildings
                        val remoteBuildings = supabaseClient.postgrest["buildings"]
                            .select().decodeList<BuildingRemoteDto>()
                        
                        withContext(Dispatchers.IO) {
                            remoteBuildings.forEach { dto ->
                                queries.insertBuilding(
                                    id = dto.id,
                                    name = dto.name,
                                    description = dto.description,
                                    isAvailable = if (dto.is_available) 1L else 0L
                                )
                            }
                        }

                        // 2. Sync Rooms
                        val remoteRooms = supabaseClient.postgrest["rooms"]
                            .select().decodeList<RoomRemoteDto>()
                        
                        withContext(Dispatchers.IO) {
                            remoteRooms.forEach { dto ->
                                queries.insertRoom(
                                    id = dto.id,
                                    buildingId = dto.building_id,
                                    name = dto.name,
                                    floor = dto.floor.toLong(),
                                    status = dto.status,
                                    type = "REGULAR", // Default or map if needed
                                    capacity = dto.capacity.toLong(),
                                    hasAc = if (dto.has_ac) 1L else 0L,
                                    hasProjector = if (dto.has_projector) 1L else 0L,
                                    borrowerName = null,
                                    maintenanceDescription = null
                                )
                            }
                        }
                    } catch (e: Exception) {
                        // Silent fail
                    }
                }
            }
        }
    }

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

    override fun searchRoomsFiltered(
        query: String,
        minCapacity: Int,
        maxCapacity: Int
    ): Flow<List<Room>> {
        return queries.searchRoomsFiltered(
            query = query,
            minCapacity = minCapacity.toLong(),
            maxCapacity = maxCapacity.toLong()
        ).asFlow()
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

    override suspend fun updateRoomStatus(
        roomId: String,
        status: RoomStatus,
        borrowerName: String?,
        maintenanceDescription: String?
    ) {
        withContext(Dispatchers.IO) {
            try {
                if (networkMonitor.isOnline.value) {
                    supabaseClient.postgrest["rooms"].update(mapOf("status" to status.name)) {
                        filter { eq("id", roomId) }
                    }
                }
                queries.updateRoomStatus(
                    status = status.name,
                    borrowerName = borrowerName,
                    maintenanceDescription = maintenanceDescription,
                    id = roomId
                )
            } catch (e: Exception) {
                // local fallback
                queries.updateRoomStatus(status.name, borrowerName, maintenanceDescription, roomId)
            }
        }
    }

    private fun RoomEntity.toDomain(): Room {
        return Room(
            id = id,
            buildingId = buildingId,
            name = name,
            floor = floor.toInt(),
            status = try { RoomStatus.valueOf(status) } catch (e: Exception) { RoomStatus.AVAILABLE },
            type = try { RoomType.valueOf(type) } catch (e: Exception) { RoomType.REGULAR },
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

                // Seed GKU1 Rooms
                queries.insertRoom(
                    id = "GKU1-101",
                    buildingId = "GKU1",
                    name = "101",
                    floor = 1L,
                    status = RoomStatus.AVAILABLE.name,
                    type = RoomType.REGULAR.name,
                    capacity = 45L,
                    hasAc = 1L,
                    hasProjector = 1L,
                    borrowerName = null,
                    maintenanceDescription = null
                )

                // Seed GEDUNG-E Rooms
                queries.insertRoom(
                    id = "GEDUNG-E-LAB01",
                    buildingId = "GEDUNG-E",
                    name = "LAB 01",
                    floor = 1L,
                    status = RoomStatus.MAINTENANCE.name,
                    type = RoomType.REGULAR.name,
                    capacity = 35L,
                    hasAc = 1L,
                    hasProjector = 0L,
                    borrowerName = null,
                    maintenanceDescription = "Upgrade Komputer"
                )

                // Seed GKU2 Rooms with VARYING CAPACITIES (35-60)
                for (f in 1..3) {
                    for (i in 1..25) {
                        val roomNum = f * 100 + i
                        val status = when {
                            roomNum % 7 == 0 -> RoomStatus.MAINTENANCE
                            roomNum % 3 == 0 -> RoomStatus.BOOKED
                            else -> RoomStatus.AVAILABLE
                        }
                        
                        // New Variation Logic: pseudo-random between 35 and 60
                        val variedCapacity = 35L + (roomNum % 26L)

                        queries.insertRoom(
                            id = "GKU2-$roomNum",
                            buildingId = "GKU2",
                            name = roomNum.toString(),
                            floor = f.toLong(),
                            status = status.name,
                            type = RoomType.REGULAR.name,
                            capacity = variedCapacity,
                            hasAc = 1L,
                            hasProjector = 1L,
                            borrowerName = if (status == RoomStatus.BOOKED) "Mata Kuliah PAM - Dosen X" else null,
                            maintenanceDescription = if (status == RoomStatus.MAINTENANCE) "Perbaikan AC Sentral" else null
                        )
                    }
                }
                for (i in 1..20) {
                    val roomNum = 400 + i
                    val variedCapacity = 35L + ((roomNum * 3) % 26L)
                    queries.insertRoom(
                        id = "GKU2-$roomNum",
                        buildingId = "GKU2",
                        name = roomNum.toString(),
                        floor = 4L,
                        status = RoomStatus.AVAILABLE.name,
                        type = RoomType.REGULAR.name,
                        capacity = variedCapacity,
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
