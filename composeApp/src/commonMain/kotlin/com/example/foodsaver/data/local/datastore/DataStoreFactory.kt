package com.example.foodsaver.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * Factory untuk membuat DataStore Preferences berdasarkan platform.
 */
expect class DataStoreFactory {
    fun create(): DataStore<Preferences>
}

/**
 * Nama file untuk DataStore Preferences.
 */
const val DATASTORE_FILE_NAME = "foodsaver.preferences_pb"
