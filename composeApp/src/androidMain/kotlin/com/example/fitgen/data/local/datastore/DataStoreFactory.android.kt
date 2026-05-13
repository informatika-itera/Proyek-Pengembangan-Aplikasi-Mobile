package com.example.fitgen.data.local.datastore

import android.content.Context

/**
 * Android implementation of [DataStoreFactory].
 *
 * Menyimpan file preferences di internal storage aplikasi
 * (`/data/data/<package>/files/`).
 */
actual class DataStoreFactory(
    private val context: Context
) {
    actual fun producePath(): String = context.filesDir.absolutePath
}
