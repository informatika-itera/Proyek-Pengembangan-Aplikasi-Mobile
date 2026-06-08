package com.example.fitkos

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestScope

fun createTestDataStore(coroutineScope: CoroutineScope): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        scope = coroutineScope,
        produceFile = { "test.preferences_pb".toPath() }
    )
}
