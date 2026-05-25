package com.kosthub.app.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.kosthub.app.platform.PlatformContext

expect class DataStoreFactory(platformContext: PlatformContext) {
    fun createDataStore(): DataStore<Preferences>
}
