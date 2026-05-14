package com.example.foodsaver.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

/**
 * Platform-specific factory for DataStore<Preferences>.
 */
expect class DataStoreFactory {
    /** Returns the absolute path where preferences are stored. */
    fun producePath(): String
}

internal const val DATA_STORE_FILE_NAME = "foodsaver.preferences_pb"

/**
 * Creates DataStore<Preferences> from platform-specific [DataStoreFactory].
 */
fun DataStoreFactory.create(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { "${producePath()}/$DATA_STORE_FILE_NAME".toPath() }
    )
}
