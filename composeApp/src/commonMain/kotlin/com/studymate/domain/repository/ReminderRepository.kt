package com.studymate.domain.repository

import com.studymate.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getAllReminders(): Flow<List<Reminder>>
    suspend fun insertReminder(reminder: Reminder): Long
    suspend fun deleteReminder(id: Long)
}
