package com.studymate.data.repository

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import com.studymate.domain.repository.CalendarEvent
import com.studymate.domain.repository.CalendarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class AndroidCalendarRepository(private val context: Context) : CalendarRepository {

    override suspend fun getEvents(): Result<List<CalendarEvent>> = withContext(Dispatchers.IO) {
        try {
            val events = mutableListOf<CalendarEvent>()
            val projection = arrayOf(
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.DISPLAY_COLOR,
                CalendarContract.Events.CALENDAR_DISPLAY_NAME
            )

            val cursor = context.contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                null,
                null,
                "${CalendarContract.Events.DTSTART} ASC"
            )

            cursor?.use {
                while (it.moveToNext()) {
                    val calName = it.getString(7) ?: ""
                    val isHoliday = calName.contains("Holiday", ignoreCase = true) || 
                                   calName.contains("Libur", ignoreCase = true)
                    
                    events.add(
                        CalendarEvent(
                            id = it.getLong(0).toString(),
                            title = it.getString(1) ?: "No Title",
                            description = it.getString(2),
                            location = it.getString(3),
                            startTime = it.getLong(4),
                            endTime = it.getLong(5),
                            color = String.format("#%06X", 0xFFFFFF and it.getInt(6)),
                            isGoogleEvent = true,
                            isHoliday = isHoliday
                        )
                    )
                }
            }
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addEvent(event: CalendarEvent): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val calendarId = getPrimaryCalendarId() ?: 1L
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, event.startTime)
                put(CalendarContract.Events.DTEND, event.endTime)
                put(CalendarContract.Events.TITLE, event.title)
                put(CalendarContract.Events.DESCRIPTION, event.description)
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            }
            context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getPrimaryCalendarId(): Long? {
        val projection = arrayOf(CalendarContract.Calendars._ID, CalendarContract.Calendars.IS_PRIMARY, CalendarContract.Calendars.ACCOUNT_NAME)
        val cursor = context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        var firstId: Long? = null
        cursor?.use {
            if (it.moveToFirst()) {
                firstId = it.getLong(0)
                do {
                    if (it.getInt(1) == 1) return it.getLong(0)
                } while (it.moveToNext())
            }
        }
        return firstId
    }

    override suspend fun deleteEvent(eventId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val deleteUri = CalendarContract.Events.CONTENT_URI.buildUpon()
                .appendPath(eventId).build()
            context.contentResolver.delete(deleteUri, null, null)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncWithGoogle(): Result<Unit> {
        // OS handles sync for CalendarContract
        return Result.success(Unit)
    }
}
