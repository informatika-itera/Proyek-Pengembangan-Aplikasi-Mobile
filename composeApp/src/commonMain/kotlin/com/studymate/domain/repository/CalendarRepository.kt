package com.studymate.domain.repository

interface CalendarRepository {
    suspend fun getEvents(): Result<List<CalendarEvent>>
    suspend fun addEvent(event: CalendarEvent): Result<Unit>
    suspend fun deleteEvent(eventId: String): Result<Unit>
    suspend fun syncWithGoogle(): Result<Unit>
}

data class CalendarEvent(
    val id: String? = null,
    val title: String,
    val description: String? = null,
    val location: String? = null,
    val startTime: Long,
    val endTime: Long,
    val color: String? = null,
    val isGoogleEvent: Boolean = false,
    val isHoliday: Boolean = false
)
