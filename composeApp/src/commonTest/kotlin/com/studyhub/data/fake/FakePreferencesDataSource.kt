package com.studyhub.data.fake

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.studyhub.data.local.PreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePreferencesDataSource : PreferencesDataSource(
    object : DataStore<Preferences> {
        override val data: Flow<Preferences> = MutableStateFlow(emptyPreferences())
        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences = emptyPreferences()
    }
) {
    private val _autoDelete = MutableStateFlow(true)
    private val _maxCount = MutableStateFlow(100)
    var lastCleanup: Long = 0L

    var autoDelete: Boolean
        get() = _autoDelete.value
        set(value) { _autoDelete.value = value }

    var maxCount: Int
        get() = _maxCount.value
        set(value) { _maxCount.value = value }

    override val notifAutoDeleteEnabled: Flow<Boolean> = _autoDelete
    override val notifMaxHistoryCount: Flow<Int> = _maxCount

    override suspend fun getLastNotifCleanup(): Long = lastCleanup
    override suspend fun setLastNotifCleanup(timestamp: Long) { lastCleanup = timestamp }
}
