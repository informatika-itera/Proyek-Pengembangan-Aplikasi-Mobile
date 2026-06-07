package com.studyhub.domain.repository

import com.studyhub.domain.model.AiUsageStats
import com.studyhub.domain.model.PriorityResult
import com.studyhub.domain.model.ReminderSchedule
import com.studyhub.domain.model.Task

sealed class AiError : Exception() {
    class NoInternet : AiError()
    class QuotaExceeded : AiError()
    data class ApiError(val code: Int, override val message: String?) : AiError()
    class ParseError : AiError()
}

interface AiRepository {

    suspend fun getSmartPriority(
        tasks: List<Task>
    ): List<PriorityResult>

    suspend fun getSmartReminder(
        task: Task,
        completedHistory: List<Task>
    ): ReminderSchedule

    suspend fun getAiUsageStats(): AiUsageStats

    suspend fun clearPriorityCache()
}
