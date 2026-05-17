package com.example.tabungin.data.local.datastore

import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask


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
