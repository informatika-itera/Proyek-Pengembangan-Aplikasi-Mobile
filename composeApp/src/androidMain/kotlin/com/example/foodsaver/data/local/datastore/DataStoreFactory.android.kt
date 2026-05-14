package com.example.foodsaver.data.local.datastore

import android.content.Context

/**
 * Android implementation of [DataStoreFactory].
 */
actual class DataStoreFactory(
    private val context: Context
) {
    actual fun producePath(): String = context.filesDir.absolutePath
}
