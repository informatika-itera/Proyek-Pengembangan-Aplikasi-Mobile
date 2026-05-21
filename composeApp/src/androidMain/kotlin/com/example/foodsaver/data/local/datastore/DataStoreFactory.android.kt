package com.example.foodsaver.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import java.io.File

/**
 * Implementasi Android untuk DataStoreFactory.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DataStoreFactory(private val context: Context) {
    actual fun create(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = {
                File(context.filesDir, DATASTORE_FILE_NAME)
            }
        )
    }
}
