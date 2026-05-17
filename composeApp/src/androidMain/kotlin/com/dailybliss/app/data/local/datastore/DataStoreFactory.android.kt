package com.dailybliss.app.data.local.datastore

import com.dailybliss.app.core.util.PlatformContext

/**
 * Android implementation of [DataStoreFactory].
 *
 * Menyimpan file preferences di internal storage aplikasi
 * (`/data/data/<package>/files/`).
 */
actual class DataStoreFactory actual constructor(
    private val context: PlatformContext
) {
    actual fun producePath(): String = context.filesDir.absolutePath
}

