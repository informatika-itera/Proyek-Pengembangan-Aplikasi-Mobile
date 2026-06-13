package com.example.fitgen.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeDataStore : DataStore<Preferences> {
    private val _data = MutableStateFlow<Preferences>(emptyPreferences())
    override val data: Flow<Preferences> = _data

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val newPrefs = transform(_data.value)
        _data.value = newPrefs
        return newPrefs
    }
}
