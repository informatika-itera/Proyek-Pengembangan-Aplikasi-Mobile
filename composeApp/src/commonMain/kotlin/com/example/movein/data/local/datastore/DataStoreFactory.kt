package com.example.movein.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

interface DataStoreFactory {
    fun producePath(): String
}

internal const val DATA_STORE_FILE_NAME = "movein.preferences_pb"

fun DataStoreFactory.create(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { "${producePath()}/$DATA_STORE_FILE_NAME".toPath() }
    )
}