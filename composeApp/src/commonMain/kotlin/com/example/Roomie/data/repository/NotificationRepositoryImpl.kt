package com.example.Roomie.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.domain.model.Notification
import com.example.Roomie.domain.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NotificationRepositoryImpl(
    database: RoomieDatabase
) : NotificationRepository {
    private val queries = database.notificationQueries

    override fun getAllNotifications(): Flow<List<Notification>> {
        return queries.getAllNotifications()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Notification(
                        id = entity.id,
                        title = entity.title,
                        message = entity.message,
                        timestamp = entity.timestamp,
                        isRead = entity.isRead != 0L
                    )
                }
            }
    }

    override suspend fun addNotification(notification: Notification) {
        withContext(Dispatchers.IO) {
            queries.insertNotification(
                id = notification.id,
                title = notification.title,
                message = notification.message,
                timestamp = notification.timestamp,
                isRead = if (notification.isRead) 1L else 0L
            )
        }
    }

    override suspend fun markAsRead(id: String) {
        withContext(Dispatchers.IO) {
            queries.markAsRead(id)
        }
    }
}
