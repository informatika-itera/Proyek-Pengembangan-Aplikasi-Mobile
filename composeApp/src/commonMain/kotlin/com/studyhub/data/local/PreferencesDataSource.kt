package com.studyhub.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.studyhub.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

object PreferencesKeys {
    val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    val USER_NAME = stringPreferencesKey("user_name")
    val DEFAULT_VIEW = stringPreferencesKey("default_view")
    val DEFAULT_SORT_BY = stringPreferencesKey("default_sort_by")
    val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
    val IS_AI_REMINDER = booleanPreferencesKey("is_ai_reminder")
    val POMODORO_FOCUS = intPreferencesKey("pomodoro_focus")
    val POMODORO_SHORT_BREAK = intPreferencesKey("pomodoro_short_break")
    val POMODORO_LONG_BREAK = intPreferencesKey("pomodoro_long_break")
}

class PreferencesDataSource(
    private val dataStore: DataStore<Preferences>
) {
    // ── Dark Mode ──
    val isDarkMode: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            prefs[PreferencesKeys.IS_DARK_MODE] ?: false
        }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_DARK_MODE] = enabled
        }
    }

    // ── User Name ──
    val userName: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            prefs[PreferencesKeys.USER_NAME] ?: "Pelajar"
        }

    suspend fun setUserName(name: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.USER_NAME] = name
        }
    }

    // ── Notification ──
    val notificationEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            prefs[PreferencesKeys.NOTIFICATION_ENABLED] ?: true
        }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.NOTIFICATION_ENABLED] = enabled
        }
    }

    // ── AI Reminder ──
    val isAiReminderEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            prefs[PreferencesKeys.IS_AI_REMINDER] ?: true
        }

    suspend fun setAiReminderEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_AI_REMINDER] = enabled
        }
    }

    // ── Pomodoro ──
    val pomodoroFocus: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            prefs[PreferencesKeys.POMODORO_FOCUS] ?: 25
        }

    val pomodoroShortBreak: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            prefs[PreferencesKeys.POMODORO_SHORT_BREAK] ?: 5
        }

    val pomodoroLongBreak: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            prefs[PreferencesKeys.POMODORO_LONG_BREAK] ?: 15
        }

    suspend fun setPomodoroSettings(
        focus: Int, shortBreak: Int, longBreak: Int
    ) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.POMODORO_FOCUS] = focus
            prefs[PreferencesKeys.POMODORO_SHORT_BREAK] = shortBreak
            prefs[PreferencesKeys.POMODORO_LONG_BREAK] = longBreak
        }
    }

    // ── Get all as UserPreferences snapshot ──
    val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            UserPreferences(
                userName = prefs[PreferencesKeys.USER_NAME] ?: "Pelajar",
                isDarkMode = prefs[PreferencesKeys.IS_DARK_MODE] ?: false,
                notificationEnabled = prefs[PreferencesKeys.NOTIFICATION_ENABLED] ?: true,
                isAiReminderEnabled = prefs[PreferencesKeys.IS_AI_REMINDER] ?: true,
                pomodoroFocusDuration = prefs[PreferencesKeys.POMODORO_FOCUS] ?: 25,
                pomodoroShortBreak = prefs[PreferencesKeys.POMODORO_SHORT_BREAK] ?: 5,
                pomodoroLongBreak = prefs[PreferencesKeys.POMODORO_LONG_BREAK] ?: 15
            )
        }
}
