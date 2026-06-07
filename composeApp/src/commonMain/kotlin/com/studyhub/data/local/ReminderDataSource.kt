package com.studyhub.data.local

import com.studyhub.database.StudyHubDatabase
import com.studyhub.domain.model.ReminderInfo
import com.studyhub.core.util.currentTimeMillis

class ReminderDataSource(
    private val database: StudyHubDatabase
) {
    fun upsert(
        taskId: String,
        scheduledAt: Long,
        aiReason: String
    ) = try {
        database.reminderEntityQueries.upsert(
            taskId, scheduledAt, aiReason, 1,
            currentTimeMillis()
        )
    } catch (e: Exception) { }

    fun deactivate(taskId: String) = try {
        database.reminderEntityQueries.deactivate(taskId)
    } catch (e: Exception) { }

    fun deactivateAll() = try {
        database.reminderEntityQueries.deactivateAll()
    } catch (e: Exception) { }

    fun getAllActive(): List<ReminderInfo> = try {
        database.reminderEntityQueries.selectAllActive()
            .executeAsList()
            .map {
                ReminderInfo(
                    taskId = it.taskId,
                    scheduledAt = it.scheduledAt,
                    aiReason = it.aiReason,
                    isActive = it.isActive == 1L,
                    createdAt = it.createdAt
                )
            }
    } catch (e: Exception) {
        emptyList()
    }

    fun getByTaskId(taskId: String): ReminderInfo? = try {
        database.reminderEntityQueries.selectByTaskId(taskId)
            .executeAsOneOrNull()?.let {
                ReminderInfo(
                    it.taskId, it.scheduledAt,
                    it.aiReason, it.isActive == 1L,
                    it.createdAt
                )
            }
    } catch (e: Exception) { null }

    fun deleteByTaskId(taskId: String) = try {
        database.reminderEntityQueries.deleteByTaskId(taskId)
    } catch (e: Exception) { }
}
