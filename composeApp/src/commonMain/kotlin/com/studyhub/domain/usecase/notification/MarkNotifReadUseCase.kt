package com.studyhub.domain.usecase.notification

import com.studyhub.domain.repository.NotifHistoryRepository

class MarkNotifReadUseCase(
    private val notifHistoryRepository: NotifHistoryRepository
) {
    suspend operator fun invoke(id: String? = null) {
        if (id == null) notifHistoryRepository.markAllRead()
        else notifHistoryRepository.markRead(id)
    }
}
