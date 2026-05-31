package com.studyhub.data.local

import com.studyhub.database.StudyHubDatabase
import com.studyhub.domain.model.AiUsageStats
import com.studyhub.database.AiCacheEntity
import kotlinx.datetime.*
import com.studyhub.core.util.currentTimeMillis

object AiCacheTTL {
    const val PRIORITY_HOURS = 6L
    const val REMINDER_HOURS = 24L

    fun priorityExpiry() =
        currentTimeMillis() + PRIORITY_HOURS * 3_600_000
    fun reminderExpiry() =
        currentTimeMillis() + REMINDER_HOURS * 3_600_000
}

object AiUsageLimit {
    const val MAX_PRIORITY_PER_DAY = 10
    const val MAX_REMINDER_PER_DAY = 20
    const val MIN_TASKS_FOR_AI = 2
}

class AiCacheDataSource(private val database: StudyHubDatabase) {

    fun getValidCache(cacheKey: String): AiCacheEntity? = try {
        database.aiCacheEntityQueries
            .selectValidCache(cacheKey, currentTimeMillis())
            .executeAsOneOrNull()
    } catch (e: Exception) {
        null
    }

    fun insertCache(
        cacheKey: String,
        result: String,
        promptType: String,
        expiresAt: Long
    ) {
        try {
            database.aiCacheEntityQueries.insertCache(
                cacheKey = cacheKey,
                result = result,
                promptType = promptType,
                createdAt = currentTimeMillis(),
                expiresAt = expiresAt
            )
        } catch (e: Exception) {
            // Table might not exist yet
        }
    }

    fun deleteExpiredCache() {
        try {
            database.aiCacheEntityQueries
                .deleteExpiredCache(currentTimeMillis())
        } catch (e: Exception) {
        }
    }

    fun getUsageStats(): AiUsageStats {
        return try {
            val today = getTodayDateString()
            val usage = database.aiUsageEntityQueries
                .getUsage()
                .executeAsOneOrNull()

            if (usage == null || usage.lastResetDate != today) {
                database.aiUsageEntityQueries.upsertUsage(
                    priorityCallsToday = 0,
                    reminderCallsToday = 0,
                    lastResetDate = today
                )
                AiUsageStats(0, 0, today)
            } else {
                AiUsageStats(
                    priorityCallsToday = usage.priorityCallsToday.toInt(),
                    reminderCallsToday = usage.reminderCallsToday.toInt(),
                    lastResetDate = usage.lastResetDate
                )
            }
        } catch (e: Exception) {
            // Table might not exist yet — return safe defaults
            AiUsageStats(0, 0, getTodayDateString())
        }
    }

    fun incrementPriorityUsage() {
        try {
            ensureTodayUsage()
            database.aiUsageEntityQueries.incrementPriority()
        } catch (e: Exception) {
            // Silent fail
        }
    }

    fun incrementReminderUsage() {
        try {
            ensureTodayUsage()
            database.aiUsageEntityQueries.incrementReminder()
        } catch (e: Exception) {
            // Silent fail
        }
    }

    fun canCallPriority(): Boolean = try {
        val stats = getUsageStats()
        stats.priorityCallsToday < AiUsageLimit.MAX_PRIORITY_PER_DAY
    } catch (e: Exception) {
        true // Allow if can't check
    }

    fun canCallReminder(): Boolean = try {
        val stats = getUsageStats()
        stats.reminderCallsToday < AiUsageLimit.MAX_REMINDER_PER_DAY
    } catch (e: Exception) {
        true
    }

    private fun ensureTodayUsage() {
        val today = getTodayDateString()
        val usage = database.aiUsageEntityQueries
            .getUsage().executeAsOneOrNull()
        if (usage == null || usage.lastResetDate != today) {
            database.aiUsageEntityQueries.upsertUsage(0, 0, today)
        }
    }

    private fun getTodayDateString(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return "${now.year}-${now.monthNumber.toString().padStart(2, '0')}-${now.dayOfMonth.toString().padStart(2, '0')}"
    }
}
