package com.studyhub.data.local

import com.studyhub.database.StudyHubDatabase
import com.studyhub.core.util.uuid
import com.studyhub.database.PomodoroSessionEntity

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

    fun getSessionsInRange(start: Long, end: Long): List<PomodoroSessionEntity> = try {
        database.pomodoroSessionEntityQueries
            .selectSessionsInRange(start, end)
            .executeAsList()
    } catch (e: Exception) { emptyList() }

    fun getFocusMinutesInRange(start: Long, end: Long): Int = try {
        database.pomodoroSessionEntityQueries
            .selectTotalFocusMinutesInRange(start, end)
            .executeAsOne().total?.toInt() ?: 0
    } catch (e: Exception) { 0 }
}
