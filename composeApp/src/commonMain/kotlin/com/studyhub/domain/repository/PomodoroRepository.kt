package com.studyhub.domain.repository

import com.studyhub.domain.model.PomodoroPhase

interface PomodoroRepository {
    suspend fun saveSession(
        taskId: String?,
        taskTitle: String?,
        durationMinutes: Int,
        phase: PomodoroPhase,
        wasCompleted: Boolean
    )
    suspend fun getFocusMinutesInRange(start: Long, end: Long): Int
    suspend fun getSessionsInRange(start: Long, end: Long): List<PomodoroSessionSummary>
    suspend fun getTodayFocusMinutes(): Int
}

data class PomodoroSessionSummary(
    val id: String,
    val taskTitle: String?,
    val durationMinutes: Int,
    val phase: PomodoroPhase,
    val completedAt: Long,
    val wasCompleted: Boolean
)
