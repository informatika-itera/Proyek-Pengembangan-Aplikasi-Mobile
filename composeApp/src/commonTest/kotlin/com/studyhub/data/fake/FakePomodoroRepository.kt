package com.studyhub.data.fake

import com.studyhub.domain.model.PomodoroPhase
import com.studyhub.domain.repository.PomodoroRepository
import com.studyhub.domain.repository.PomodoroSessionSummary

class FakePomodoroRepository : PomodoroRepository {
    var focusMinutesToday = 0
    var focusCountToday = 0
    var saveSessionCalled = false

    override suspend fun saveSession(
        taskId: String?, taskTitle: String?,
        durationMinutes: Int, phase: PomodoroPhase,
        wasCompleted: Boolean
    ) { saveSessionCalled = true }

    override suspend fun getTodayFocusCount() = focusCountToday
    override suspend fun getTodayFocusMinutes() = focusMinutesToday
    override suspend fun getTodaySessions() = emptyList<PomodoroSessionSummary>()
}
