package com.example.Roomie.domain.usecase

import com.example.Roomie.domain.model.Notification
import com.example.Roomie.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow

class GetAllNotificationsUseCase(private val repository: NotificationRepository) {
    operator fun invoke(): Flow<List<Notification>> = repository.getAllNotifications()
}

class MarkNotificationAsReadUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(id: String) = repository.markAsRead(id)
}
