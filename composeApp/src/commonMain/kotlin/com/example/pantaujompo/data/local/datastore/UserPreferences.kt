package com.example.pantaujompo.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        val HAS_COMPLETED_PROFILE = booleanPreferencesKey("has_completed_profile_v2")
        val USER_NAME = stringPreferencesKey("user_name_v2")
        val USER_AGE = intPreferencesKey("user_age_v2")
        val USER_WEIGHT = floatPreferencesKey("user_weight_v2")
        val USER_HEIGHT = floatPreferencesKey("user_height_v2")
        val USER_GENDER = stringPreferencesKey("user_gender_v2") // Laki-laki / Perempuan
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode_v2")
        val PROFILE_IMAGE_URI = stringPreferencesKey("profile_image_uri_v2")
        val TARGET_KALORI = intPreferencesKey("target_kalori_v2")
        val LANGUAGE = stringPreferencesKey("app_language_v2") // "id" atau "en"
        val TEXT_SIZE_SCALE = floatPreferencesKey("text_size_scale_v2") // 1.0f normal, 1.2f besar
    }

    // Baca status apakah profil udah diisi
    val hasCompletedProfile: Flow<Boolean> = dataStore.data.map { it[HAS_COMPLETED_PROFILE] ?: false }

    // BACA DATA PROFIL
    val userName: Flow<String> = dataStore.data.map { it[USER_NAME] ?: "" }
    val userAge: Flow<Int> = dataStore.data.map { it[USER_AGE] ?: 0 }
    val userWeight: Flow<Float> = dataStore.data.map { it[USER_WEIGHT] ?: 0f }
    val userHeight: Flow<Float> = dataStore.data.map { it[USER_HEIGHT] ?: 0f }
    val userGender: Flow<String> = dataStore.data.map { it[USER_GENDER] ?: "Laki-laki" }
    
    // BACA SETTINGS
    val isDarkMode: Flow<Boolean> = dataStore.data.map { it[IS_DARK_MODE] ?: true } // Default Dark
    val profileImageUri: Flow<String> = dataStore.data.map { it[PROFILE_IMAGE_URI] ?: "" }
    val targetKalori: Flow<Int> = dataStore.data.map { it[TARGET_KALORI] ?: 2000 } // Default 2000 kcal
    val language: Flow<String> = dataStore.data.map { it[LANGUAGE] ?: "id" }
    val textSizeScale: Flow<Float> = dataStore.data.map { it[TEXT_SIZE_SCALE] ?: 1.0f }

    // FUNGSI SAVE PROFIL
    suspend fun saveProfile(name: String, age: Int, weight: Float, height: Float, gender: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = name
            preferences[USER_AGE] = age
            preferences[USER_WEIGHT] = weight
            preferences[USER_HEIGHT] = height
            preferences[USER_GENDER] = gender
            preferences[HAS_COMPLETED_PROFILE] = true // Tandai udah ngisi!
        }
    }
    
    suspend fun setDarkMode(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }
    
    suspend fun setProfileImage(uri: String) {
        dataStore.edit { preferences ->
            preferences[PROFILE_IMAGE_URI] = uri
        }
    }
    
    suspend fun setTargetKalori(kalori: Int) {
        dataStore.edit { preferences ->
            preferences[TARGET_KALORI] = kalori
        }
    }
    
    suspend fun setLanguage(lang: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE] = lang
        }
    }
    
    suspend fun setTextSizeScale(scale: Float) {
        dataStore.edit { preferences ->
            preferences[TEXT_SIZE_SCALE] = scale
        }
    }
    
    suspend fun clearAllData() {
        dataStore.edit { it.clear() }
    }
}