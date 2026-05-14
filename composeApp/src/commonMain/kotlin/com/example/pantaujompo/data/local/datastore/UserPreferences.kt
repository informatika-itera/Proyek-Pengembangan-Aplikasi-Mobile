package com.example.pantaujompo.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val dataStore: DataStore<Preferences>) {

    // Kunci Brankas
    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_AGE = stringPreferencesKey("user_age")
        val USER_WEIGHT = stringPreferencesKey("user_weight")
        val USER_HEIGHT = stringPreferencesKey("user_height")
    }

    // Fungsi Baca (Read) Data Permanen
    val userName: Flow<String> = dataStore.data.map { it[USER_NAME] ?: "Zahwa Natasya H." }
    val userAge: Flow<String> = dataStore.data.map { it[USER_AGE] ?: "20" }
    val userWeight: Flow<String> = dataStore.data.map { it[USER_WEIGHT] ?: "55" }
    val userHeight: Flow<String> = dataStore.data.map { it[USER_HEIGHT] ?: "160" }

    // Fungsi Simpan (Write) Data Permanen
    suspend fun saveProfile(name: String, age: String, weight: String, height: String) {
        dataStore.edit { prefs ->
            prefs[USER_NAME] = name
            prefs[USER_AGE] = age
            prefs[USER_WEIGHT] = weight
            prefs[USER_HEIGHT] = height
        }
    }
}