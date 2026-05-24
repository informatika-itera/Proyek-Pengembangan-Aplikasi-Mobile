package com.studyhub.core.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "studyhub_preferences")

fun createDataStore(context: Context): DataStore<Preferences> = context.dataStore
