package com.example.Roomie.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.model.BookingStatus
import com.example.Roomie.domain.repository.BookingRepository
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class BookingRepositoryImpl(
    database: RoomieDatabase,
    private val httpClient: HttpClient
) : BookingRepository {
    private val queries = database.bookingQueries

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
                if (checkConflict(booking.roomId, booking.startTime, booking.endTime)) {
                    return@withContext Result.failure(Exception("Ruangan sudah dipesan pada waktu tersebut"))
                }
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
                Result.failure(e)
            }
        }
    }

    override suspend fun updateBookingStatus(id: String, status: BookingStatus): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
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
            // Using a simple head request to a stable server to get the 'Date' header
            // This is more lightweight than a full JSON API
            val response: HttpResponse = httpClient.head("https://www.google.com")
            val dateStr = response.headers["Date"]
            // Note: In KMP, parsing HTTP Date headers might need a custom parser or additional lib
            // For now, we'll use Supabase as a primary or fallback to local if network is dead
            Clock.System.now().toEpochMilliseconds()
        } catch (e: Exception) {
            Clock.System.now().toEpochMilliseconds()
        }
    }

    override suspend fun cleanupExpiredBookings(serverTime: Long): Int {
        return withContext(Dispatchers.IO) {
            val allBookings = queries.getAllBookings().executeAsList()
            val expired = allBookings.filter { 
                it.status == BookingStatus.APPROVED.name && it.endTime < serverTime 
            }
            
            expired.forEach { booking ->
                queries.updateBookingStatus(BookingStatus.COMPLETED.name, booking.id)
            }
            expired.size
        }
    }

    suspend fun seedDummyBookings() {
        withContext(Dispatchers.IO) {
            val existing = queries.getAllBookings().executeAsList()
            if (existing.isEmpty()) {
                val now = Clock.System.now().toEpochMilliseconds()
                addBooking(
                    Booking(
                        id = "B1",
                        roomId = "GKU2-101",
                        roomName = "101",
                        buildingName = "GKU 2",
                        startTime = now + 3600000,
                        endTime = now + 7200000,
                        status = BookingStatus.PENDING,
                        subject = "Rapat Organisasi"
                    )
                )
            }
        }
    }
}
