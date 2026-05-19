package com.example.foodsaver.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

/**
 * Implementasi Android untuk DataStoreFactory.
 */
actual class DataStoreFactory(private val context: Context) {
    actual fun create(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = {
                context.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath.toPath()
            }
        )
    }
}
