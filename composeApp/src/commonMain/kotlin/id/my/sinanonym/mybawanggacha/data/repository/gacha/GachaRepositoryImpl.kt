package id.my.sinanonym.mybawanggacha.data.repository.gacha

import id.my.sinanonym.mybawanggacha.data.local.datastore.UserPreferences
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaHistoryEntry
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaPreference
import id.my.sinanonym.mybawanggacha.domain.gacha.repository.GachaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class GachaRepositoryImpl(
    private val userPreferences: UserPreferences
) : GachaRepository {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override fun observeLastPreference(): Flow<GachaPreference> {
        return userPreferences.gachaPreferenceJson.map { value ->
            value.decodePreferenceOrDefault()
        }
    }

    override fun observeHistory(): Flow<List<GachaHistoryEntry>> {
        return userPreferences.gachaHistoryJson.map { value ->
            value.decodeHistoryOrEmpty()
        }
    }

    override suspend fun getLastPreference(): GachaPreference {
        return userPreferences.gachaPreferenceJson.first().decodePreferenceOrDefault()
    }

    override suspend fun saveLastPreference(preference: GachaPreference) {
        userPreferences.setGachaPreferenceJson(
            json.encodeToString(GachaPreference.serializer(), preference)
        )
    }

    override suspend fun saveHistoryEntry(entry: GachaHistoryEntry) {
        val current = userPreferences.gachaHistoryJson.first().decodeHistoryOrEmpty()
        val updated = (listOf(entry) + current)
            .distinctBy { history -> "${history.item.mediaType}:${history.item.malId}:${history.pickedAtEpochMillis}" }
            .take(GACHA_HISTORY_LIMIT)

        userPreferences.setGachaHistoryJson(
            json.encodeToString(ListSerializer(GachaHistoryEntry.serializer()), updated)
        )
    }

    override suspend fun clearHistory() {
        userPreferences.setGachaHistoryJson("[]")
    }

    private fun String.decodePreferenceOrDefault(): GachaPreference {
        if (isBlank()) return GachaPreference()

        return runCatching {
            json.decodeFromString(GachaPreference.serializer(), this)
        }.getOrDefault(GachaPreference())
    }

    private fun String.decodeHistoryOrEmpty(): List<GachaHistoryEntry> {
        if (isBlank()) return emptyList()

        return runCatching {
            json.decodeFromString(ListSerializer(GachaHistoryEntry.serializer()), this)
        }.getOrDefault(emptyList())
    }

    private companion object {
        const val GACHA_HISTORY_LIMIT = 20
    }
}
