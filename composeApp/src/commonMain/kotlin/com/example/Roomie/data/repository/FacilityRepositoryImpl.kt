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
import io.github.jan.supabase.realtime.*
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
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import io.github.aakira.napier.Napier

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
                        // 1. Initial Sync Buildings
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

                        // 2. Initial Sync Rooms
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
                                    type = "REGULAR", 
                                    capacity = dto.capacity.toLong(),
                                    hasAc = if (dto.has_ac) 1L else 0L,
                                    hasProjector = if (dto.has_projector) 1L else 0L,
                                    borrowerName = null,
                                    maintenanceDescription = null
                                )
                            }
                        }

                        // 3. Realtime Listener
                        supabaseClient.realtime.connect()
                        val channel = supabaseClient.realtime.channel("facility-sync")
                        
                        val buildingsFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") { table = "buildings" }
                        val roomsFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") { table = "rooms" }

                        channel.subscribe()

                        launch {
                            buildingsFlow.collect { change ->
                                withContext(Dispatchers.IO) {
                                    when (change) {
                                        is PostgresAction.Insert -> {
                                            val dto = change.decodeRecord<BuildingRemoteDto>()
                                            queries.insertBuilding(dto.id, dto.name, dto.description, if (dto.is_available) 1L else 0L)
                                        }
                                        is PostgresAction.Update -> {
                                            val dto = change.decodeRecord<BuildingRemoteDto>()
                                            queries.insertBuilding(dto.id, dto.name, dto.description, if (dto.is_available) 1L else 0L)
                                        }
                                        is PostgresAction.Delete -> {
                                            val id = change.oldRecord["id"]?.jsonPrimitive?.contentOrNull
                                            if (id != null) queries.deleteBuilding(id)
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        }

                        launch {
                            roomsFlow.collect { change ->
                                withContext(Dispatchers.IO) {
                                    when (change) {
                                        is PostgresAction.Insert, is PostgresAction.Update -> {
                                            val dto = if (change is PostgresAction.Insert) change.decodeRecord<RoomRemoteDto>() else (change as PostgresAction.Update).decodeRecord<RoomRemoteDto>()
                                            queries.insertRoom(
                                                id = dto.id,
                                                buildingId = dto.building_id,
                                                name = dto.name,
                                                floor = dto.floor.toLong(),
                                                status = dto.status,
                                                type = "REGULAR",
                                                capacity = dto.capacity.toLong(),
                                                hasAc = if (dto.has_ac) 1L else 0L,
                                                hasProjector = if (dto.has_projector) 1L else 0L,
                                                borrowerName = null,
                                                maintenanceDescription = null
                                            )
                                        }
                                        is PostgresAction.Delete -> {
                                            val id = change.oldRecord["id"]?.jsonPrimitive?.contentOrNull
                                            if (id != null) queries.deleteRoom(id)
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        }

                    } catch (e: Exception) {
                        Napier.e("Facility Sync Error: ${e.message}", e)
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
                Napier.e("Update Room Status Error: ${e.message}", e)
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
        // ... seed logic (already handled by observeSync usually, but kept for first run)
    }
}
