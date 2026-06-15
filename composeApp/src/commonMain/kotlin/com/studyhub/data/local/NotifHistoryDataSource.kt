package com.studyhub.data.local

import com.studyhub.database.StudyHubDatabase
import com.studyhub.domain.model.NotifHistoryItem
import com.studyhub.domain.model.NotifType
import com.studyhub.core.util.currentTimeMillis
import com.studyhub.core.util.uuid
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface NotifHistoryDataSource {
    fun getAll(): List<NotifHistoryItem>
    fun getUnreadCount(): Int
    fun observeUnreadCount(): Flow<Int>
    fun insert(taskId: String, taskTitle: String, taskSubject: String, aiReason: String, type: NotifType)
    fun markAllRead()
    fun markRead(id: String)
    fun deleteById(id: String)
    fun deleteAll()
    fun getCount(): Int
    fun deleteOlderThan(timestamp: Long)
    fun deleteExcessItems(max: Int)
}

class SqlDelightNotifHistoryDataSource(
    private val database: StudyHubDatabase
) : NotifHistoryDataSource {
    override fun getAll(): List<NotifHistoryItem> = try {
        database.notifHistoryEntityQueries.selectAll()
            .executeAsList()
            .map {
                NotifHistoryItem(
                    it.id, it.taskId, it.taskTitle,
                    it.taskSubject, it.aiReason,
                    it.sentAt, it.isRead == 1L,
                    type = try { NotifType.valueOf(it.type) } catch (e: Exception) { NotifType.TASK }
                )
            }
    } catch (e: Exception) {
        println("NotifHistoryDataSource: getAll failed: ${e.message}")
        emptyList()
    }

    override fun getUnreadCount(): Int = try {
        database.notifHistoryEntityQueries.selectUnreadCount()
            .executeAsOne().toInt()
    } catch (e: Exception) { 0 }

    override fun observeUnreadCount(): Flow<Int> =
        database.notifHistoryEntityQueries.selectUnreadCount()
            .asFlow()
            .mapToOne(Dispatchers.IO)
            .map { it.toInt() }

    override fun insert(
        taskId: String, taskTitle: String,
        taskSubject: String, aiReason: String,
        type: NotifType
    ) {
        try {
            database.notifHistoryEntityQueries.insert(
                id = uuid(),
                taskId = taskId,
                taskTitle = taskTitle,
                taskSubject = taskSubject,
                aiReason = aiReason,
                sentAt = currentTimeMillis(),
                type = type.name
            )
        } catch (e: Exception) {
            println("NotifHistoryDataSource: Insert failed: ${e.message}")
        }
    }

    override fun markAllRead() = try {
        database.notifHistoryEntityQueries.markAllRead()
    } catch (e: Exception) { }

    override fun markRead(id: String) = try {
        database.notifHistoryEntityQueries.markRead(id)
    } catch (e: Exception) { }

    override fun deleteById(id: String) = try {
        database.notifHistoryEntityQueries.deleteById(id)
    } catch (e: Exception) { }

    override fun deleteAll() = try {
        database.notifHistoryEntityQueries.deleteAll()
    } catch (e: Exception) { }

    override fun getCount(): Int = try {
        database.notifHistoryEntityQueries.countAll().executeAsOne().toInt()
    } catch (e: Exception) { 0 }

    override fun deleteOlderThan(timestamp: Long) = try {
        database.notifHistoryEntityQueries.deleteOlderThan(timestamp)
    } catch (e: Exception) { }

    override fun deleteExcessItems(max: Int) = try {
        database.notifHistoryEntityQueries.deleteExcessItems(max.toLong())
    } catch (e: Exception) { }
}
