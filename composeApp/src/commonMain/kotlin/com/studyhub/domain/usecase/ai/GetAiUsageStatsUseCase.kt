package com.studyhub.domain.usecase.ai

import com.studyhub.domain.model.AiUsageStats
import com.studyhub.domain.repository.AiRepository

class GetAiUsageStatsUseCase(
    private val aiRepository: AiRepository
) {
    suspend operator fun invoke(): AiUsageStats =
        aiRepository.getAiUsageStats()
}
