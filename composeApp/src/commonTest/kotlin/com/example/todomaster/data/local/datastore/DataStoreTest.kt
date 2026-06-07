package com.example.todomaster.data.local.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertTrue

class DataStoreTest {
    @Test
    fun `test user preferences toggle`() = runTest {
        val datastore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { "test.preferences_pb".toPath() }
        )
        val prefs = UserPreferences(datastore)

        prefs.setDarkMode(true)
        val isDark = prefs.isDarkMode.first()

        assertTrue(isDark)
    }
}