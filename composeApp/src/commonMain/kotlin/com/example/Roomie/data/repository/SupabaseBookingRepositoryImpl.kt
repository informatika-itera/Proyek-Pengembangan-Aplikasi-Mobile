package com.example.Roomie.data.repository

import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.model.BookingStatus
import com.example.Roomie.domain.repository.BookingRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseBookingDto(
    val id: String,
    val room_id: String,
    val room_name: String,
    val building_name: String,
    val start_time: Long,
    val end_time: Long,
    val status: String,
    val subject: String? = null,
    val user_id: String? = null
)

class SupabaseBookingRepositoryImpl(
    private val client: SupabaseClient
) : BookingRepository {

    @OptIn(SupabaseExperimental::class)
    override fun getAllBookings(): Flow<List<Booking>> {
        return client.postgrest["bookings"].selectAsFlow(
            primaryKey = SupabaseBookingDto::id
        ).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addBooking(booking: Booking): Result<Unit> {
        return try {
            // 🛡️ CLOUD CONFLICT GUARD (MULYA'S LOGIC)
            // 1. Check if there's any approved booking in Cloud that overlaps this time
            val isConflict = checkConflict(booking.roomId, booking.startTime, booking.endTime)
            if (isConflict) {
                return Result.failure(Exception("Waduh, telat Bre! Ruangan ini baru aja di-book orang lain."))
            }

            // 2. No conflict? Proceed to insert
            client.postgrest["bookings"].insert(booking.toDto())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBookingStatus(id: String, status: BookingStatus): Result<Unit> {
        return try {
            client.postgrest["bookings"].update(mapOf("status" to status.name)) {
                filter { eq("id", id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBooking(id: String): Result<Unit> {
        return try {
            client.postgrest["bookings"].delete {
                filter { eq("id", id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkConflict(roomId: String, startTime: Long, endTime: Long): Boolean {
        return try {
            // Fetch only APPROVED bookings for this specific room from Cloud
            val response = client.postgrest["bookings"].select {
                filter {
                    eq("room_id", roomId)
                    eq("status", BookingStatus.APPROVED.name)
                }
            }
            val approvedBookings = response.decodeList<SupabaseBookingDto>()
            
            // Logic Overlap: (StartA < EndB) AND (EndA > StartB)
            approvedBookings.any { it.start_time < endTime && it.end_time > startTime }
        } catch (e: Exception) {
            // If network fails, better be safe and return false (or handle accordingly)
            false
        }
    }

    override suspend fun getServerTime(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }

    override suspend fun cleanupExpiredBookings(serverTime: Long): Int {
        return 0 
    }

    private fun SupabaseBookingDto.toDomain() = Booking(
        id = id,
        roomId = room_id,
        roomName = room_name,
        buildingName = building_name,
        startTime = start_time,
        endTime = end_time,
        status = BookingStatus.valueOf(status),
        subject = subject,
        userId = user_id
    )

    private fun Booking.toDto() = SupabaseBookingDto(
        id = id,
        room_id = roomId,
        room_name = roomName,
        building_name = buildingName,
        start_time = startTime,
        end_time = endTime,
        status = status.name,
        subject = subject,
        user_id = userId
    )
}
