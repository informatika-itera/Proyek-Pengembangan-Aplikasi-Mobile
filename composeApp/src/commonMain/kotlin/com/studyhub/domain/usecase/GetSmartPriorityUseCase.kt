package com.studyhub.domain.usecase

import com.studyhub.domain.model.PriorityResult
import com.studyhub.domain.repository.AiRepository
import com.studyhub.domain.repository.TaskRepository

class GetSmartPriorityUseCase(
    private val aiRepository: AiRepository,
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(userId: String): List<PriorityResult> {
        val activeTasks = taskRepository.getActiveTasks(userId)
        return aiRepository.getSmartPriority(activeTasks)
    }
}
