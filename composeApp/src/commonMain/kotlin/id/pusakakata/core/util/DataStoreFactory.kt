package id.pusakakata.core.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect class DataStoreFactory {
    fun create(): DataStore<Preferences>
}

const val DATASTORE_FILE_NAME = "pusaka_kata.preferences_pb"
