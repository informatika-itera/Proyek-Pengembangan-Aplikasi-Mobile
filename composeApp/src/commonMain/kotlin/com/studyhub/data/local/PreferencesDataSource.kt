package com.studyhub.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.studyhub.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
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
    val POMODORO_SESSIONS_BEFORE_LONG = intPreferencesKey("pomodoro_sessions_before_long")
    
    val NOTIF_AUTO_DELETE_ENABLED = booleanPreferencesKey("notif_auto_delete_enabled")
    val NOTIF_MAX_HISTORY_COUNT = intPreferencesKey("notif_max_history_count")
    val LAST_NOTIF_CLEANUP = longPreferencesKey("last_notif_cleanup")
}

open class PreferencesDataSource(
    private val dataStore: DataStore<Preferences>
) {
    // ── Dark Mode ──
    open val isDarkMode: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            prefs[PreferencesKeys.IS_DARK_MODE] ?: false
        }

    open suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_DARK_MODE] = enabled
        }
    }

    // ── User Name ──
    open val userName: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            prefs[PreferencesKeys.USER_NAME] ?: "Pelajar"
        }

    open suspend fun setUserName(name: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.USER_NAME] = name
        }
    }

    // ── Notifications ──
    open val notificationEnabled: Flow<Boolean> = dataStore.data
        .map { prefs ->
            prefs[PreferencesKeys.NOTIFICATION_ENABLED] ?: true
        }

    open suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.NOTIFICATION_ENABLED] = enabled
        }
    }

    open val isAiReminderEnabled: Flow<Boolean> = dataStore.data
        .map { prefs ->
            prefs[PreferencesKeys.IS_AI_REMINDER] ?: true
        }

    open suspend fun setAiReminderEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_AI_REMINDER] = enabled
        }
    }

    // ── Pomodoro ──
    open val pomodoroFocusDuration: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            prefs[PreferencesKeys.POMODORO_FOCUS] ?: 25
        }

    open val pomodoroShortBreak: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            prefs[PreferencesKeys.POMODORO_SHORT_BREAK] ?: 5
        }

    open val pomodoroLongBreak: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            prefs[PreferencesKeys.POMODORO_LONG_BREAK] ?: 15
        }

    open suspend fun setPomodoroSettings(
        focus: Int, shortBreak: Int, longBreak: Int
    ) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.POMODORO_FOCUS] = focus
            prefs[PreferencesKeys.POMODORO_SHORT_BREAK] = shortBreak
            prefs[PreferencesKeys.POMODORO_LONG_BREAK] = longBreak
        }
    }

    // ── Get all as UserPreferences snapshot ──
    open val userPreferences: Flow<UserPreferences> = dataStore.data
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
                pomodoroLongBreak = prefs[PreferencesKeys.POMODORO_LONG_BREAK] ?: 15,
                pomodoroSessionsBeforeLong = prefs[PreferencesKeys.POMODORO_SESSIONS_BEFORE_LONG] ?: 4
            )
        }

    // ── Notification History ──
    open val notifAutoDeleteEnabled: Flow<Boolean> = dataStore.data.map { it[PreferencesKeys.NOTIF_AUTO_DELETE_ENABLED] ?: true }
    open val notifMaxHistoryCount: Flow<Int> = dataStore.data.map { it[PreferencesKeys.NOTIF_MAX_HISTORY_COUNT] ?: 100 }
    
    open suspend fun getLastNotifCleanup(): Long {
        return dataStore.data.first()[PreferencesKeys.LAST_NOTIF_CLEANUP] ?: 0L
    }

    open suspend fun setLastNotifCleanup(timestamp: Long) {
        dataStore.edit { it[PreferencesKeys.LAST_NOTIF_CLEANUP] = timestamp }
    }
}
