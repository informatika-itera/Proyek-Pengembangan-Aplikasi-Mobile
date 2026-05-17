package com.example.metaforge.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

open class DraftPreferences(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val LAST_DRAFT_KEY = stringPreferencesKey("last_draft")
    }

    open suspend fun saveLastDraft(draftData: String) {
        dataStore.edit { prefs ->
            prefs[LAST_DRAFT_KEY] = draftData
        }
    }

    open fun getLastDraft(): Flow<String?> = dataStore.data.map { prefs ->
        prefs[LAST_DRAFT_KEY]
    }

    open suspend fun clearDraft() {
        dataStore.edit { prefs ->
            prefs.remove(LAST_DRAFT_KEY)
        }
    }
}