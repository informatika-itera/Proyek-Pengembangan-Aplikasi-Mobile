package com.studyhub.data.repository

import com.studyhub.data.local.PomodoroDataSource
import com.studyhub.domain.model.PomodoroPhase
import com.studyhub.domain.repository.PomodoroRepository
import com.studyhub.domain.repository.PomodoroSessionSummary
import com.studyhub.core.util.currentTimeMillis
import com.studyhub.core.util.atStartOfDayMillis
import com.studyhub.core.util.toLocalDate

class PomodoroRepositoryImpl(
    private val dataSource: PomodoroDataSource
) : PomodoroRepository {
    override suspend fun saveSession(
        taskId: String?,
        taskTitle: String?,
        durationMinutes: Int,
        phase: PomodoroPhase,
        wasCompleted: Boolean
    ) {
        dataSource.insertSession(
            taskId = taskId,
            taskTitle = taskTitle,
            durationMinutes = durationMinutes,
            phase = phase.name,
            completedAt = currentTimeMillis(),
            wasCompleted = wasCompleted
        )
    }

    override suspend fun getFocusMinutesInRange(start: Long, end: Long): Int {
        return dataSource.getFocusMinutesInRange(start, end)
    }

    override suspend fun getTodayFocusMinutes(): Int {
        val now = currentTimeMillis()
        val startOfDay = now.toLocalDate().atStartOfDayMillis()
        return getFocusMinutesInRange(startOfDay, now)
    }

    override suspend fun getSessionsInRange(start: Long, end: Long): List<PomodoroSessionSummary> {
        return dataSource.getSessionsInRange(start, end).map {
            PomodoroSessionSummary(
                id = it.id,
                taskTitle = it.taskTitle,
                durationMinutes = it.durationMinutes.toInt(),
                phase = PomodoroPhase.valueOf(it.phase),
                completedAt = it.completedAt,
                wasCompleted = it.wasCompleted == 1L
            )
        }
    }
}
