package com.dailybliss.app.data.local.datastore

import com.dailybliss.app.core.util.PlatformContext
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

/**
 * iOS implementation of [DataStoreFactory].
 *
 * Menyimpan file preferences di Documents directory aplikasi
 * (NSDocumentDirectory).
 */
actual class DataStoreFactory actual constructor(
    context: PlatformContext
) {
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

