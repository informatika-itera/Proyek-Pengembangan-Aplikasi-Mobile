package com.studyhub.domain.usecase.notification

import com.studyhub.domain.repository.NotifHistoryRepository

class DeleteNotifHistoryUseCase(
    private val notifHistoryRepository: NotifHistoryRepository
) {
    suspend operator fun invoke(id: String? = null) {
        if (id == null) notifHistoryRepository.clearAll()
        else notifHistoryRepository.deleteItem(id)
    }
}
