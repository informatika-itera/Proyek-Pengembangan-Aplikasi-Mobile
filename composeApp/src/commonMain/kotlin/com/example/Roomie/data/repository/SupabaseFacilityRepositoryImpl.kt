package com.example.Roomie.data.repository

import com.example.Roomie.domain.model.*
import com.example.Roomie.domain.repository.FacilityRepository
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable

@Serializable
data class RoomDto(
    val id: String,
    val building_id: String,
    val name: String,
    val floor: Int,
    val status: String,
    val capacity: Int,
    val has_ac: Boolean,
    val has_projector: Boolean,
    val borrower_name: String? = null,
    val maintenance_description: String? = null
)

@Serializable
data class BuildingDto(
    val id: String,
    val name: String,
    val description: String,
    val is_available: Boolean
)

class SupabaseFacilityRepositoryImpl(
    private val client: SupabaseClient
) : FacilityRepository {

    @OptIn(SupabaseExperimental::class)
    override fun getBuildings(): Flow<List<Building>> {
        return client.postgrest["buildings"].selectAsFlow(
            primaryKey = BuildingDto::id
        ).map { list -> list.map { it.toDomain() } }
         .catch { emit(emptyList()) }
    }

    @OptIn(SupabaseExperimental::class)
    override fun getRoomsByFloor(buildingId: String, floor: Int): Flow<List<Room>> {
        return client.postgrest["rooms"].selectAsFlow(
            primaryKey = RoomDto::id
        ).map { list -> 
            list.filter { it.building_id == buildingId && it.floor == floor }
                .map { it.toDomain() } 
        }.catch { emit(emptyList()) }
    }

    @OptIn(SupabaseExperimental::class)
    override fun getRoomsByBuilding(buildingId: String): Flow<List<Room>> {
        return client.postgrest["rooms"].selectAsFlow(
            primaryKey = RoomDto::id
        ).map { list -> 
            list.filter { it.building_id == buildingId }
                .map { it.toDomain() } 
        }.catch { emit(emptyList()) }
    }

    @OptIn(SupabaseExperimental::class)
    override fun searchRooms(query: String): Flow<List<Room>> {
        return client.postgrest["rooms"].selectAsFlow(
            primaryKey = RoomDto::id
        ).map { list -> 
            list.filter { it.name.contains(query, ignoreCase = true) }
                .map { it.toDomain() } 
        }.catch { emit(emptyList()) }
    }

    @OptIn(SupabaseExperimental::class)
    override fun searchRoomsFiltered(query: String, minCapacity: Int, maxCapacity: Int): Flow<List<Room>> {
        return client.postgrest["rooms"].selectAsFlow(
            primaryKey = RoomDto::id
        ).map { list -> 
            list.filter { 
                it.name.contains(query, ignoreCase = true) && 
                it.capacity in minCapacity..maxCapacity 
            }.map { it.toDomain() } 
        }.catch { emit(emptyList()) }
    }

    @OptIn(SupabaseExperimental::class)
    override fun getRoomById(roomId: String): Flow<Room?> {
        return client.postgrest["rooms"].selectAsFlow(
            primaryKey = RoomDto::id
        ).map { list -> 
            list.find { it.id == roomId }?.toDomain() 
        }.catch { emit(null) }
    }

    override suspend fun updateRoomStatus(roomId: String, status: RoomStatus, borrowerName: String?, maintenanceDescription: String?) {
        try {
            // Kita coba update status dulu karena ini kolom yang pasti ada
            client.postgrest["rooms"].update(mapOf("status" to status.name)) {
                filter { eq("id", roomId) }
            }
            
            // Coba update kolom tambahan secara terpisah agar tidak gagal semua jika kolom tidak ada
            try {
                client.postgrest["rooms"].update(
                    mapOf(
                        "borrower_name" to borrowerName,
                        "maintenance_description" to maintenanceDescription
                    )
                ) {
                    filter { eq("id", roomId) }
                }
            } catch (e: Exception) {
                Napier.w("Supabase: Kolom tambahan mungkin tidak ada di database: ${e.message}")
            }
            
            Napier.d("Supabase: Berhasil update status ruangan $roomId ke ${status.name}")
        } catch (e: Exception) {
            Napier.e("Supabase Update Error: ${e.message}", e)
        }
    }

    private fun RoomDto.toDomain() = Room(
        id = id,
        buildingId = building_id,
        name = name,
        floor = floor,
        status = RoomStatus.valueOf(status),
        type = RoomType.REGULAR,
        capacity = capacity,
        hasAc = has_ac,
        hasProjector = has_projector,
        borrowerName = borrower_name,
        maintenanceDescription = maintenance_description
    )

    private fun BuildingDto.toDomain() = Building(
        id = id,
        name = name,
        description = description,
        isAvailable = is_available
    )
}
