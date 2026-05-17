package com.example.tabungin.data.local.datastore

import android.content.Context

actual class DataStoreFactory(
    private val context: Context
) {
    actual fun producePath(): String = context.filesDir.absolutePath
}
