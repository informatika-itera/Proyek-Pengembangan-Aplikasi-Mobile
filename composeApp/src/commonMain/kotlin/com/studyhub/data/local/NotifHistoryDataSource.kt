package com.studyhub.data.local

import com.studyhub.database.StudyHubDatabase
import com.studyhub.domain.model.NotifHistoryItem
import com.studyhub.core.util.currentTimeMillis
import com.studyhub.core.util.uuid

interface NotifHistoryDataSource {
    fun getAll(): List<NotifHistoryItem>
    fun getUnreadCount(): Int
    fun insert(taskId: String, taskTitle: String, taskSubject: String, aiReason: String)
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
                    it.sentAt, it.isRead == 1L
                )
            }
    } catch (e: Exception) { emptyList() }

    override fun getUnreadCount(): Int = try {
        database.notifHistoryEntityQueries.selectUnreadCount()
            .executeAsOne().toInt()
    } catch (e: Exception) { 0 }

    override fun insert(
        taskId: String, taskTitle: String,
        taskSubject: String, aiReason: String
    ) = try {
        database.notifHistoryEntityQueries.insert(
            uuid(),
            taskId, taskTitle, taskSubject,
            aiReason, currentTimeMillis()
        )
    } catch (e: Exception) { }

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
