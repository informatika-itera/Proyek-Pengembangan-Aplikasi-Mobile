package com.example.pantaujompo.data.local.datastore

import com.example.pantaujompo.PantauJompoApplication

actual class DataStoreFactory {
    actual fun producePath(): String {
        return PantauJompoApplication.appContext.filesDir.resolve("user_preferences.preferences_pb").absolutePath
    }
}