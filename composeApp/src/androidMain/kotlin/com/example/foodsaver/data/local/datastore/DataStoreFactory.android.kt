package com.example.foodsaver.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile

/**
 * Implementasi Android untuk DataStoreFactory.
 */
actual class DataStoreFactory(private val context: Context) {
    actual fun create(): DataStore<Preferences> {
        // Implementasi ini biasanya menggunakan bit of logic untuk inisialisasi DataStore
        // Di KMP, biasanya kita menggunakan PreferenceDataStoreFactory.create
        // Untuk saat ini, kita pastikan strukturnya benar agar build passing.
        return androidx.datastore.preferences.core.PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(DATASTORE_FILE_NAME) }
        )
    }
}
