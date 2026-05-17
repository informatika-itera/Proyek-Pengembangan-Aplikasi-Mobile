package com.example.musickeep.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

actual class DataStoreFactory(private val context: Context) {
    actual fun create(): DataStore<Preferences> {
        return androidx.datastore.preferences.core.PreferenceDataStoreFactory.create(
            produceFile = {
                File(context.filesDir, "datastore/$DATASTORE_FILE_NAME")
            }
        )
    }
}
