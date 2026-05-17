package com.example.musickeep.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect class DataStoreFactory {
    fun create(): DataStore<Preferences>
}

const val DATASTORE_FILE_NAME = "musickeep.preferences_pb"
