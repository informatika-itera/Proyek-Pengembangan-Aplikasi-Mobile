package com.example.Roomie.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.model.BookingStatus
import com.example.Roomie.domain.repository.BookingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BookingRepositoryImpl(
    database: RoomieDatabase
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
                        status = try { BookingStatus.valueOf(entity.status) } catch (e: Exception) { BookingStatus.UPCOMING },
                        subject = entity.subject
                    )
                }
            }
    }

    override suspend fun addBooking(booking: Booking) {
        withContext(Dispatchers.IO) {
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
        }
    }

    override suspend fun deleteBooking(id: String) {
        withContext(Dispatchers.IO) {
            queries.deleteBooking(id)
        }
    }

    suspend fun seedDummyBookings() {
        withContext(Dispatchers.IO) {
            val existing = queries.getAllBookings().executeAsList()
            if (existing.isEmpty()) {
                val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
                addBooking(
                    Booking(
                        id = "B1",
                        roomId = "GKU2-101",
                        roomName = "101",
                        buildingName = "GKU 2",
                        startTime = now + 3600000,
                        endTime = now + 7200000,
                        status = BookingStatus.UPCOMING,
                        subject = "Rapat Organisasi"
                    )
                )
                addBooking(
                    Booking(
                        id = "B2",
                        roomId = "GKU2-201",
                        roomName = "201",
                        buildingName = "GKU 2",
                        startTime = now + 86400000,
                        endTime = now + 86400000 + 3600000,
                        status = BookingStatus.UPCOMING,
                        subject = "PAM - Praktikum"
                    )
                )
            }
        }
    }
}
