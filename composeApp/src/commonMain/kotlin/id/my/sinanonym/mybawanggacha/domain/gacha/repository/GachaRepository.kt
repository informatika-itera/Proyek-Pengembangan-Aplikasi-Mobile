package id.my.sinanonym.mybawanggacha.domain.gacha.repository

import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaHistoryEntry
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaPreference
import kotlinx.coroutines.flow.Flow

interface GachaRepository {
    fun observeLastPreference(): Flow<GachaPreference>
    fun observeHistory(): Flow<List<GachaHistoryEntry>>

    suspend fun getLastPreference(): GachaPreference
    suspend fun saveLastPreference(preference: GachaPreference)
    suspend fun saveHistoryEntry(entry: GachaHistoryEntry)
    suspend fun clearHistory()
}
