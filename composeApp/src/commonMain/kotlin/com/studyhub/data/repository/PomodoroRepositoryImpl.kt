package com.studyhub.data.repository

import com.studyhub.data.local.PomodoroDataSource
import com.studyhub.domain.model.PomodoroPhase
import com.studyhub.domain.repository.PomodoroRepository
import com.studyhub.domain.repository.PomodoroSessionSummary
import com.studyhub.core.util.currentTimeMillis
import com.studyhub.core.util.atStartOfDayMillis
import kotlinx.datetime.*

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

    override suspend fun getTodayFocusCount(): Int {
        return dataSource.getTodayFocusCount(getStartOfToday())
    }

    override suspend fun getTodayFocusMinutes(): Int {
        return dataSource.getTodayFocusMinutes(getStartOfToday())
    }

    override suspend fun getTodaySessions(): List<PomodoroSessionSummary> = emptyList()

    private fun getStartOfToday(): Long {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return today.atStartOfDayMillis()
    }
}
