package com.example.neurodeck.data.local.datastore

import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask


@Suppress("CAST_NEVER_SUCCEEDS")
actual class DataStoreFactory {
    actual fun producePath(): String {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory?.path) {
            "Failed to get iOS documents directory path"
        }
    }
}