package com.example.noteai.data.local.datastore

import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

/**
 * iOS implementation of [DataStoreFactory].
 *
 * Menyimpan file preferences di Documents directory aplikasi
 * (NSDocumentDirectory).
 */
actual class DataStoreFactory {
    actual fun producePath(): String {
        val paths = NSSearchPathForDirectoriesInDomains(
            directory = NSDocumentDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true
        )
        return requireNotNull(paths.firstOrNull() as? String) {
            "Tidak bisa menemukan Documents directory di iOS"
        }
    }
}
