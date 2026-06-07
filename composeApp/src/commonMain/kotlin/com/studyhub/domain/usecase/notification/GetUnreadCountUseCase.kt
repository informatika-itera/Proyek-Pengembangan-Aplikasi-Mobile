package com.studyhub.domain.usecase.notification

import com.studyhub.domain.repository.NotifHistoryRepository

class GetUnreadCountUseCase(
    private val notifHistoryRepository: NotifHistoryRepository
) {
    suspend operator fun invoke(): Int =
        notifHistoryRepository.getUnreadCount()
}
