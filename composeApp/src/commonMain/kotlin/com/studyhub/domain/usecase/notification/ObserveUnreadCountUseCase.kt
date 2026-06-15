package com.studyhub.domain.usecase.notification

import com.studyhub.domain.repository.NotifHistoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveUnreadCountUseCase(private val repository: NotifHistoryRepository) {
    operator fun invoke(): Flow<Int> = repository.observeUnreadCount()
}
