package com.studyhub.core.util

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.core.DataStore
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import okio.Path.Companion.toPath

fun createDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            val documentDirectory = NSFileManager.defaultManager
                .URLForDirectory(
                    NSDocumentDirectory,
                    NSUserDomainMask,
                    null,
                    true,
                    null
                )
            (requireNotNull(documentDirectory).path + "/studyhub_preferences.preferences_pb").toPath()
        }
    )
