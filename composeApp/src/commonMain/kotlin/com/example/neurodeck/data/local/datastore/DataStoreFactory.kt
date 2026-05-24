package com.example.neurodeck.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

/**
 * Cross-platform DataStore factory.
 *
 * Pattern expect/actual: tiap platform provide `producePath()` untuk lokasi
 * file. CommonMain pakai path itu untuk bikin DataStore instance dengan
 * PreferenceDataStoreFactory (KMP-compatible, dari datastore-preferences-core).
 *
 * Penting: file extension WAJIB `.preferences_pb` — itu format protobuf
 * yang dipakai DataStore internally. Kalau pakai extension lain, library
 * akan crash dengan IllegalArgumentException.
 */
expect class DataStoreFactory {
    fun producePath(): String
}

/**
 * Filename untuk DataStore preferences NeuroDeck.
 * Single file untuk semua app preferences (profile + theme + future settings).
 */
private const val PREFERENCES_FILENAME = "neurodeck.preferences_pb"

/**
 * Create DataStore<Preferences> instance. Dipanggil sekali di Koin
 * sebagai singleton — jangan buat instance baru di runtime karena DataStore
 * tidak boleh ada 2 instance untuk file yang sama (akan crash).
 *
 * Migration: untuk Sprint 2 belum perlu — kalau nanti schema preferences
 * berubah (rename key, dll), bisa pass `migrations = listOf(...)` di sini.
 */
fun createDataStore(factory: DataStoreFactory): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { "${factory.producePath()}/$PREFERENCES_FILENAME".toPath() },
    )