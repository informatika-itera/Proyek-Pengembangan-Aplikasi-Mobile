package com.studyhub.domain.usecase.ai

import com.studyhub.domain.model.PriorityResult
import com.studyhub.domain.repository.AiRepository
import com.studyhub.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first

class GetSmartPriorityUseCase(
    private val aiRepository: AiRepository,
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(): List<PriorityResult> {
        val activeTasks = taskRepository.getActiveTasks().first()
        if (activeTasks.isEmpty()) return emptyList()
        return aiRepository.getSmartPriority(activeTasks)
    }
}
