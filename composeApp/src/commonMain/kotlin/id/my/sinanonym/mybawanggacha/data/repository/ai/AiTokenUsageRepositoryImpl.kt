package id.my.sinanonym.mybawanggacha.data.repository.ai

import id.my.sinanonym.mybawanggacha.data.local.datastore.UserPreferences
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiModel
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiModelTokenUsage
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiTokenUsageDelta
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiTokenUsageSnapshot
import id.my.sinanonym.mybawanggacha.domain.settings.repository.AiTokenUsageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Clock

class AiTokenUsageRepositoryImpl(
    private val userPreferences: UserPreferences
) : AiTokenUsageRepository {

    override val usage: Flow<AiTokenUsageSnapshot> = userPreferences.aiTokenUsageJson.map { value ->
        decode(value).toDomain()
    }

    override suspend fun recordUsage(
        model: AiApiModel,
        usage: AiTokenUsageDelta
    ) {
        if (usage.totalTokens <= 0 && usage.promptTokens <= 0 && usage.candidatesTokens <= 0) {
            return
        }

        val current = decode(userPreferences.aiTokenUsageJson.first())
        val currentEntry = current.entries[model.name] ?: StoredAiTokenUsageEntry()
        val updatedEntry = currentEntry.copy(
            requestCount = currentEntry.requestCount + 1L,
            promptTokens = currentEntry.promptTokens + usage.promptTokens.toLong(),
            candidatesTokens = currentEntry.candidatesTokens + usage.candidatesTokens.toLong(),
            thoughtsTokens = currentEntry.thoughtsTokens + usage.thoughtsTokens.toLong(),
            cachedContentTokens = currentEntry.cachedContentTokens + usage.cachedContentTokens.toLong(),
            totalTokens = currentEntry.totalTokens + usage.totalTokens.toLong(),
            lastPromptTokens = usage.promptTokens,
            lastCandidatesTokens = usage.candidatesTokens,
            lastThoughtsTokens = usage.thoughtsTokens,
            lastCachedContentTokens = usage.cachedContentTokens,
            lastTotalTokens = usage.totalTokens,
            updatedAtMillis = Clock.System.now().toEpochMilliseconds()
        )
        val updated = current.copy(
            entries = current.entries + (model.name to updatedEntry)
        )

        userPreferences.setAiTokenUsageJson(json.encodeToString(StoredAiTokenUsageSnapshot.serializer(), updated))
    }

    override suspend fun resetUsage() {
        userPreferences.setAiTokenUsageJson("")
    }

    private fun decode(value: String): StoredAiTokenUsageSnapshot {
        if (value.isBlank()) return StoredAiTokenUsageSnapshot()
        return runCatching {
            json.decodeFromString(StoredAiTokenUsageSnapshot.serializer(), value)
        }.getOrDefault(StoredAiTokenUsageSnapshot())
    }

    private fun StoredAiTokenUsageSnapshot.toDomain(): AiTokenUsageSnapshot {
        return AiTokenUsageSnapshot(
            entries = AiApiModel.entries.map { model ->
                val entry = entries[model.name] ?: StoredAiTokenUsageEntry()
                AiModelTokenUsage(
                    model = model,
                    requestCount = entry.requestCount,
                    promptTokens = entry.promptTokens,
                    candidatesTokens = entry.candidatesTokens,
                    thoughtsTokens = entry.thoughtsTokens,
                    cachedContentTokens = entry.cachedContentTokens,
                    totalTokens = entry.totalTokens,
                    lastPromptTokens = entry.lastPromptTokens,
                    lastCandidatesTokens = entry.lastCandidatesTokens,
                    lastThoughtsTokens = entry.lastThoughtsTokens,
                    lastCachedContentTokens = entry.lastCachedContentTokens,
                    lastTotalTokens = entry.lastTotalTokens,
                    updatedAtMillis = entry.updatedAtMillis
                )
            }
        )
    }

    @Serializable
    private data class StoredAiTokenUsageSnapshot(
        val entries: Map<String, StoredAiTokenUsageEntry> = emptyMap()
    )

    @Serializable
    private data class StoredAiTokenUsageEntry(
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
    )

    private companion object {
        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }
}
