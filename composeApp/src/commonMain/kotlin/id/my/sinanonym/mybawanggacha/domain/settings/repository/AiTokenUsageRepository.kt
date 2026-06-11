package id.my.sinanonym.mybawanggacha.domain.settings.repository

import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiModel
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiTokenUsageDelta
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiTokenUsageSnapshot
import kotlinx.coroutines.flow.Flow

interface AiTokenUsageRepository {
    val usage: Flow<AiTokenUsageSnapshot>

    suspend fun recordUsage(
        model: AiApiModel,
        usage: AiTokenUsageDelta
    )

    suspend fun resetUsage()
}
