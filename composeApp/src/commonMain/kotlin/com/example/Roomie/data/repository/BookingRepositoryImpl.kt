package com.example.Roomie.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.Roomie.core.network.NetworkMonitor
import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.model.BookingStatus
import com.example.Roomie.domain.repository.BookingRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.*
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import kotlinx.serialization.Serializable

@Serializable
data class BookingRemoteDto(
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

class BookingRepositoryImpl(
    database: RoomieDatabase,
    private val httpClient: HttpClient,
    private val supabaseClient: SupabaseClient,
    private val networkMonitor: NetworkMonitor,
    private val scope: CoroutineScope
) : BookingRepository {
    private val queries = database.bookingQueries

    init {
        // Start background sync if online
        observeRealtimeSync()
    }

    private fun observeRealtimeSync() {
        scope.launch {
            networkMonitor.isOnline.collect { online ->
                if (online) {
                    try {
                        // 1. Initial Sync
                        val remoteBookings = supabaseClient.postgrest["bookings"]
                            .select().decodeList<BookingRemoteDto>()
                        
                        withContext(Dispatchers.IO) {
                            remoteBookings.forEach { dto ->
                                queries.insertBooking(
                                    id = dto.id,
                                    roomId = dto.room_id,
                                    roomName = dto.room_name,
                                    buildingName = dto.building_name,
                                    startTime = dto.start_time,
                                    endTime = dto.end_time,
                                    status = dto.status,
                                    subject = dto.subject
                                )
                            }
                        }

                        // 2. Realtime Listener
                        supabaseClient.realtime.connect()
                        val channel = supabaseClient.realtime.channel("bookings-sync")
                        val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                            table = "bookings"
                        }
                        
                        changeFlow.collect { change ->
                            withContext(Dispatchers.IO) {
                                when (change) {
                                    is PostgresAction.Insert -> {
                                        val dto = change.decodeRecord<BookingRemoteDto>()
                                        queries.insertBooking(
                                            id = dto.id,
                                            roomId = dto.room_id,
                                            roomName = dto.room_name,
                                            buildingName = dto.building_name,
                                            startTime = dto.start_time,
                                            endTime = dto.end_time,
                                            status = dto.status,
                                            subject = dto.subject
                                        )
                                    }
                                    is PostgresAction.Update -> {
                                        val dto = change.decodeRecord<BookingRemoteDto>()
                                        queries.insertBooking(
                                            id = dto.id,
                                            roomId = dto.room_id,
                                            roomName = dto.room_name,
                                            buildingName = dto.building_name,
                                            startTime = dto.start_time,
                                            endTime = dto.end_time,
                                            status = dto.status,
                                            subject = dto.subject
                                        )
                                    }
                                    is PostgresAction.Delete -> {
                                        val id = change.oldRecord["id"]?.jsonPrimitive?.content
                                        if (id != null) queries.deleteBooking(id)
                                    }
                                    else -> {}
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Silent fail for sync
                    }
                }
            }
        }
    }

    override fun getAllBookings(): Flow<List<Booking>> {
        return queries.getAllBookings()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Booking(
                        id = entity.id,
                        roomId = entity.roomId,
                        roomName = entity.roomName,
                        buildingName = entity.buildingName,
                        startTime = entity.startTime,
                        endTime = entity.endTime,
                        status = try { BookingStatus.valueOf(entity.status) } catch (e: Exception) { BookingStatus.PENDING },
                        subject = entity.subject
                    )
                }
            }
    }

    override suspend fun addBooking(booking: Booking): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Check local conflict first
                if (checkConflict(booking.roomId, booking.startTime, booking.endTime)) {
                    return@withContext Result.failure(Exception("Ruangan sudah dipesan pada waktu tersebut (Local Check)"))
                }

                // 2. Try Remote if online
                if (networkMonitor.isOnline.value) {
                    val remoteConflict = checkRemoteConflict(booking.roomId, booking.startTime, booking.endTime)
                    if (remoteConflict) {
                        return@withContext Result.failure(Exception("Ruangan baru saja di-book orang lain di Cloud!"))
                    }
                    
                    supabaseClient.postgrest["bookings"].insert(booking.toDto())
                }

                // 3. Save to Local (Always, as single source of truth for UI)
                queries.insertBooking(
                    id = booking.id,
                    roomId = booking.roomId,
                    roomName = booking.roomName,
                    buildingName = booking.buildingName,
                    startTime = booking.startTime,
                    endTime = booking.endTime,
                    status = booking.status.name,
                    subject = booking.subject
                )
                Result.success(Unit)
            } catch (e: Exception) {
                // If remote failed but it was a network error, we could still save locally and sync later
                // For now, let's just return the error
                Result.failure(e)
            }
        }
    }

    private suspend fun checkRemoteConflict(roomId: String, startTime: Long, endTime: Long): Boolean {
        return try {
            val response = supabaseClient.postgrest["bookings"].select {
                filter {
                    eq("room_id", roomId)
                    eq("status", BookingStatus.APPROVED.name)
                }
            }
            val approved = response.decodeList<BookingRemoteDto>()
            approved.any { it.start_time < endTime && it.end_time > startTime }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateBookingStatus(id: String, status: BookingStatus): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (networkMonitor.isOnline.value) {
                    supabaseClient.postgrest["bookings"].update(mapOf("status" to status.name)) {
                        filter { eq("id", id) }
                    }
                }
                queries.updateBookingStatus(status.name, id)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun deleteBooking(id: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (networkMonitor.isOnline.value) {
                    supabaseClient.postgrest["bookings"].delete {
                        filter { eq("id", id) }
                    }
                }
                queries.deleteBooking(id)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun checkConflict(roomId: String, startTime: Long, endTime: Long): Boolean {
        return withContext(Dispatchers.IO) {
            val allBookings = queries.getAllBookings().executeAsList()
            allBookings.any { 
                it.roomId == roomId && 
                it.status == BookingStatus.APPROVED.name &&
                it.startTime < endTime && startTime < it.endTime
            }
        }
    }

    override suspend fun getServerTime(): Long {
        return try {
            val response: HttpResponse = httpClient.head("https://www.google.com")
            val dateStr = response.headers["Date"]
            if (dateStr != null) {
                parseHttpDate(dateStr)
            } else {
                Clock.System.now().toEpochMilliseconds()
            }
        } catch (e: Exception) {
            Clock.System.now().toEpochMilliseconds()
        }
    }

    private fun parseHttpDate(dateStr: String): Long {
        return try {
            // Format: Wed, 21 Oct 2015 07:28:00 GMT
            val parts = dateStr.split(" ")
            val day = parts[1].toInt()
            val month = when (parts[2]) {
                "Jan" -> Month.JANUARY; "Feb" -> Month.FEBRUARY; "Mar" -> Month.MARCH
                "Apr" -> Month.APRIL; "May" -> Month.MAY; "Jun" -> Month.JUNE
                "Jul" -> Month.JULY; "Aug" -> Month.AUGUST; "Sep" -> Month.SEPTEMBER
                "Oct" -> Month.OCTOBER; "Nov" -> Month.NOVEMBER; "Dec" -> Month.DECEMBER
                else -> Month.JANUARY
            }
            val year = parts[3].toInt()
            val timeParts = parts[4].split(":")
            val hour = timeParts[0].toInt()
            val min = timeParts[1].toInt()
            val sec = timeParts[2].toInt()
            
            val dateTime = LocalDateTime(year, month, day, hour, min, sec)
            dateTime.toInstant(TimeZone.UTC).toEpochMilliseconds()
        } catch (e: Exception) {
            Clock.System.now().toEpochMilliseconds()
        }
    }

    override suspend fun cleanupExpiredBookings(serverTime: Long): Int {
        return withContext(Dispatchers.IO) {
            val allBookings = queries.getAllBookings().executeAsList()
            val expired = allBookings.filter { entity ->
                entity.status == BookingStatus.APPROVED.name && entity.endTime < serverTime
            }
            
            for (booking in expired) {
                updateBookingStatus(booking.id, BookingStatus.COMPLETED)
            }
            expired.size
        }
    }

    private fun BookingRemoteDto.toDomain() = Booking(
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

    private fun Booking.toDto() = BookingRemoteDto(
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

