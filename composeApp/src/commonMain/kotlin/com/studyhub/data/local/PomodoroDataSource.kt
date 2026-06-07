package com.studyhub.data.local

import com.studyhub.database.StudyHubDatabase
import com.studyhub.core.util.uuid

class PomodoroDataSource(
    private val database: StudyHubDatabase
) {
    fun insertSession(
        taskId: String?,
        taskTitle: String?,
        durationMinutes: Int,
        phase: String,
        completedAt: Long,
        wasCompleted: Boolean
    ) = try {
        database.pomodoroSessionEntityQueries.insertSession(
            uuid(), taskId, taskTitle,
            durationMinutes.toLong(), phase,
            completedAt,
            if (wasCompleted) 1L else 0L
        )
    } catch (e: Exception) { }

    fun getTodayFocusCount(startOfDay: Long): Int = try {
        database.pomodoroSessionEntityQueries
            .selectTodayFocusCount(startOfDay)
            .executeAsOne().toInt()
    } catch (e: Exception) { 0 }

    fun getTodayFocusMinutes(startOfDay: Long): Int = try {
        database.pomodoroSessionEntityQueries
            .selectTotalFocusMinutesToday(startOfDay)
            .executeAsOne().total?.toInt() ?: 0
    } catch (e: Exception) { 0 }
}
