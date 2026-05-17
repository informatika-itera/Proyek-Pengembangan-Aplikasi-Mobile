package com.example.tabungin.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

expect class DataStoreFactory {
    fun producePath(): String
}

internal const val DATA_STORE_FILE_NAME = "tabungin.preferences_pb"

fun DataStoreFactory.create(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { "${producePath()}/$DATA_STORE_FILE_NAME".toPath() }
    )
}
