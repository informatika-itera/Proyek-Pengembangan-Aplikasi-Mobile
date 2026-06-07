package com.studyhub.domain.usecase.notification

import com.studyhub.domain.model.NotifHistoryItem
import com.studyhub.domain.repository.NotifHistoryRepository

class GetNotifHistoryUseCase(
    private val notifHistoryRepository: NotifHistoryRepository
) {
    suspend operator fun invoke(): List<NotifHistoryItem> =
        notifHistoryRepository.getHistory()
}
