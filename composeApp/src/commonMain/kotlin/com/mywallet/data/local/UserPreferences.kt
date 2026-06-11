package com.mywallet.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        val NAME_KEY = stringPreferencesKey("user_name")
        val BIO_KEY = stringPreferencesKey("user_bio")
        val PHONE_KEY = stringPreferencesKey("user_phone")
        val EMAIL_KEY = stringPreferencesKey("user_email")
        val BIOMETRIC_KEY = booleanPreferencesKey("biometric_enabled")
    }

    val userDataFlow: Flow<UserData> = dataStore.data.map { prefs ->
        UserData(
            name = prefs[NAME_KEY] ?: "Hanifah Hasanah",
            bio = prefs[BIO_KEY] ?: "Mahasiswa Teknik Informatika ITERA",
            phone = prefs[PHONE_KEY] ?: "0812-3456-7890",
            email = prefs[EMAIL_KEY] ?: "hanifah.123140082@student.itera.ac.id",
            isBiometricEnabled = prefs[BIOMETRIC_KEY] ?: false
        )
    }

    suspend fun updateProfile(name: String, bio: String, phone: String, email: String, biometric: Boolean) {
        dataStore.edit { prefs ->
            prefs[NAME_KEY] = name
            prefs[BIO_KEY] = bio
            prefs[PHONE_KEY] = phone
            prefs[EMAIL_KEY] = email
            prefs[BIOMETRIC_KEY] = biometric
        }
    }

    suspend fun toggleBiometric(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[BIOMETRIC_KEY] = enabled
        }
    }
}

data class UserData(
    val name: String,
    val bio: String,
    val phone: String,
    val email: String,
    val isBiometricEnabled: Boolean
)
