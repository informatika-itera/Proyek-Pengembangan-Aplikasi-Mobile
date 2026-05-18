package com.example.Roomie.domain.repository

import com.example.Roomie.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getAllNotifications(): Flow<List<Notification>>
    suspend fun addNotification(notification: Notification)
    suspend fun markAsRead(id: String)
}
