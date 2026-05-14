package com.example.pocketguard.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

/**
 * Factory platform-specific untuk DataStore<Preferences>.
 *
 * Setiap platform punya lokasi penyimpanan sendiri:
 * - Android: di-construct dengan `Context`, menulis ke `context.filesDir`
 * - iOS    : tanpa argumen, menulis ke NSDocumentDirectory
 *
 * Lihat implementasi di:
 * - `androidMain/.../DataStoreFactory.android.kt`
 * - `iosMain/.../DataStoreFactory.ios.kt`
 */
expect class DataStoreFactory {
    /** Mengembalikan path absolut (tanpa nama file) tempat preferences disimpan. */
    fun producePath(): String
}

internal const val DATA_STORE_FILE_NAME = "noteai.preferences_pb"

/**
 * Membuat DataStore<Preferences> dari [DataStoreFactory] platform-specific.
 * Dipanggil dari Koin module sebagai single instance.
 */
fun DataStoreFactory.create(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { "${producePath()}/$DATA_STORE_FILE_NAME".toPath() }
    )
}
