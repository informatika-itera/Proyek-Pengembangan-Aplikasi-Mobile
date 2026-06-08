package id.my.sinanonym.mybawanggacha.data.repository.jikan

import id.my.sinanonym.mybawanggacha.domain.settings.model.NetworkMode
import id.my.sinanonym.mybawanggacha.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.first

interface JikanCachePolicy {
    suspend fun allowsNetwork(): Boolean

    fun cacheMissMessage(resource: String): String {
        return "Offline only aktif dan cache $resource belum tersedia."
    }
}

object AlwaysOnlineJikanCachePolicy : JikanCachePolicy {
    override suspend fun allowsNetwork(): Boolean = true
}

class SettingsJikanCachePolicy(
    private val settingsRepository: SettingsRepository
) : JikanCachePolicy {
    override suspend fun allowsNetwork(): Boolean {
        return settingsRepository.networkMode.first() != NetworkMode.OfflineOnly
    }
}
