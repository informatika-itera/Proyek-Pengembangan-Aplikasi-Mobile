package com.example.nutriscan.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * DataStore fake in-memory untuk unit test.
 *
 * Penting:
 * Jangan membuat Preferences() secara langsung karena constructor Preferences bersifat internal.
 * Gunakan mutablePreferencesOf().
 */
class InMemoryDataStore : DataStore<Preferences> {

    private val _data = MutableStateFlow<Preferences>(mutablePreferencesOf())

    override val data: Flow<Preferences>
        get() = _data

    override suspend fun updateData(
        transform: suspend (t: Preferences) -> Preferences
    ): Preferences {
        val updated = transform(_data.value)
        _data.value = updated
        return updated
    }
}