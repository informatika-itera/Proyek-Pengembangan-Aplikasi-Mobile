package com.example.metaforge.data.repository

import com.example.metaforge.data.local.datastore.DraftPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

// Stub sederhana untuk testing tanpa DataStore asli
class FakeDraftPreferences : DraftPreferences(
    dataStore = throw UnsupportedOperationException("Use fake")
) {
    private val _data = MutableStateFlow<String?>(null)

    override suspend fun saveLastDraft(draftData: String) {
        _data.value = draftData
    }

    override fun getLastDraft(): Flow<String?> = _data

    override suspend fun clearDraft() {
        _data.value = null
    }
}