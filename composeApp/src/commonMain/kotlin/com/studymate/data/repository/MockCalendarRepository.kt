package com.studymate.data.repository

import com.studymate.domain.repository.CalendarEvent
import com.studymate.domain.repository.CalendarRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

class MockCalendarRepository : CalendarRepository {
    private val mockEvents = mutableListOf(
        CalendarEvent(
            id = "1",
            title = "UTS Pemrograman Mobile",
            location = "Ruang H.201",
            startTime = Clock.System.now().toEpochMilliseconds(),
            endTime = Clock.System.now().plus(2, DateTimeUnit.HOUR).toEpochMilliseconds(),
            color = "#F72585"
        )
    )

    override suspend fun getEvents(): Result<List<CalendarEvent>> {
        return Result.success(mockEvents)
    }

    override suspend fun addEvent(event: CalendarEvent): Result<Unit> {
        mockEvents.add(event)
        return Result.success(Unit)
    }

    override suspend fun deleteEvent(eventId: String): Result<Unit> {
        mockEvents.removeAll { it.id == eventId }
        return Result.success(Unit)
    }

    override suspend fun syncWithGoogle(): Result<Unit> {
        return Result.success(Unit)
    }
}
