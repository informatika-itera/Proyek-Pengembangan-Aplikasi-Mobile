package com.studymate.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.studymate.data.local.StudyMateDatabase
import com.studymate.domain.model.Reminder
import com.studymate.domain.repository.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReminderRepositoryImpl(
    private val database: StudyMateDatabase
) : ReminderRepository {
    private val queries = database.reminderQueries

    override fun getAllReminders(): Flow<List<Reminder>> {
        return queries.selectAllReminders()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list ->
                list.map {
                    Reminder(
                        id = it.id,
                        title = it.title,
                        description = it.description,
                        dueDate = it.dueDate,
                        isCompleted = it.isCompleted == 1L,
                        createdAt = it.createdAt
                    )
                }
            }
    }

    override suspend fun insertReminder(reminder: Reminder): Long {
        queries.insertReminder(
            title = reminder.title,
            description = reminder.description,
            dueDate = reminder.dueDate,
            createdAt = reminder.createdAt
        )
        return queries.lastInsertId().executeAsOne()
    }

    override suspend fun deleteReminder(id: Long) {
        queries.deleteReminder(id)
    }
}
