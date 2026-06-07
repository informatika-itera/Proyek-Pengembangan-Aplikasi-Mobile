package com.example.todomaster.data.local.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertTrue

class UserPreferencesTest {

    @Test
    fun `test dark mode preference`() = runTest {
        val testFile = "test_preferences.preferences_pb"

        val datastore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { testFile.toPath() }
        )
        val prefs = UserPreferences(datastore)

        prefs.setDarkMode(true)
        assertTrue(prefs.isDarkMode.first())
    }
}