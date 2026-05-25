package com.kosthub.app.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.kosthub.app.platform.PlatformContext
import okio.Path.Companion.toPath

actual class DataStoreFactory actual constructor(
    private val platformContext: PlatformContext
) {
    actual fun createDataStore(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = {
                platformContext.context.filesDir.resolve("kosthub.preferences_pb").absolutePath.toPath()
            }
        )
    }
}
