package id.my.sinanonym.mybawanggacha.domain.settings.model

data class AiTokenUsageDelta(
    val promptTokens: Int = 0,
    val candidatesTokens: Int = 0,
    val thoughtsTokens: Int = 0,
    val cachedContentTokens: Int = 0,
    val totalTokens: Int = 0
)

data class AiModelTokenUsage(
    val model: AiApiModel,
    val requestCount: Long = 0L,
    val promptTokens: Long = 0L,
    val candidatesTokens: Long = 0L,
    val thoughtsTokens: Long = 0L,
    val cachedContentTokens: Long = 0L,
    val totalTokens: Long = 0L,
    val lastPromptTokens: Int = 0,
    val lastCandidatesTokens: Int = 0,
    val lastThoughtsTokens: Int = 0,
    val lastCachedContentTokens: Int = 0,
    val lastTotalTokens: Int = 0,
    val updatedAtMillis: Long = 0L
) {
    val inputTokenLimit: Int
        get() = model.inputTokenLimit

    val outputTokenLimit: Int
        get() = model.outputTokenLimit

    val appOutputTokenLimit: Int
        get() = model.appOutputTokenLimit

    val effectiveOutputTokenLimit: Int
        get() = model.effectiveOutputTokenLimit

    val lastInputProgress: Float
        get() = if (inputTokenLimit <= 0) 0f else lastPromptTokens.toFloat() / inputTokenLimit.toFloat()
}

data class AiTokenUsageSnapshot(
    val entries: List<AiModelTokenUsage> = AiApiModel.entries.map { model ->
        AiModelTokenUsage(model = model)
    }
) {
    val totalRequests: Long
        get() = entries.sumOf { it.requestCount }

    val totalTokens: Long
        get() = entries.sumOf { it.totalTokens }

    companion object {
        val Empty = AiTokenUsageSnapshot()
    }
}
