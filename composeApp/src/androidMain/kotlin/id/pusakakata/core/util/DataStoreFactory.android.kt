package id.pusakakata.core.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

actual class DataStoreFactory(private val context: Context) {
    actual fun create(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = { context.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath.toPath() }
        )
    }
}
