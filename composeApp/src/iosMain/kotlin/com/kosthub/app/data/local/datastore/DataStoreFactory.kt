package com.kosthub.app.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.kosthub.app.platform.PlatformContext
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual class DataStoreFactory actual constructor(
    private val platformContext: PlatformContext
) {
    actual fun createDataStore(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = {
                val directory = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = true,
                    error = null
                )
                val path = (directory?.path ?: "") + "/kosthub.preferences_pb"
                path.toPath()
            }
        )
    }
}
