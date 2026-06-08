package id.my.sinanonym.mybawanggacha.data.repository.jikan

import id.my.sinanonym.mybawanggacha.data.remote.jikan.api.JikanRateLimiter
import id.my.sinanonym.mybawanggacha.data.remote.jikan.api.JikanRequestUsageSnapshot
import id.my.sinanonym.mybawanggacha.domain.settings.model.JikanRequestUsage
import id.my.sinanonym.mybawanggacha.domain.settings.repository.JikanRequestUsageRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class JikanRequestUsageRepositoryImpl : JikanRequestUsageRepository {
    override val usage: Flow<JikanRequestUsage> = flow {
        while (true) {
            emit(JikanRateLimiter.snapshot().toDomain())
            delay(1_000L)
        }
    }

    private fun JikanRequestUsageSnapshot.toDomain(): JikanRequestUsage {
        return JikanRequestUsage(
            usedLastSecond = usedLastSecond,
            secondLimit = secondLimit,
            usedLastMinute = usedLastMinute,
            minuteLimit = minuteLimit,
            remainingThisMinute = remainingThisMinute,
            msUntilNextRequest = msUntilNextRequest
        )
    }
}
