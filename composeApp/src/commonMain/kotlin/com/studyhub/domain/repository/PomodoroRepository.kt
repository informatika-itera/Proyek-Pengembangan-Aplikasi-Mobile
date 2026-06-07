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
    suspend fun getTodayFocusCount(): Int
    suspend fun getTodayFocusMinutes(): Int
    suspend fun getTodaySessions(): List<PomodoroSessionSummary>
}

data class PomodoroSessionSummary(
    val id: String,
    val taskTitle: String?,
    val durationMinutes: Int,
    val phase: PomodoroPhase,
    val completedAt: Long,
    val wasCompleted: Boolean
)
