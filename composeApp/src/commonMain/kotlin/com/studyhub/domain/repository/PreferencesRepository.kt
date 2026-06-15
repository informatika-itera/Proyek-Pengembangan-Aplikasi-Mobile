package com.studyhub.domain.repository

import com.studyhub.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val isDarkMode: Flow<Boolean>
    val userName: Flow<String>
    val userPreferences: Flow<UserPreferences>
    val notificationEnabled: Flow<Boolean>
    val isAiReminderEnabled: Flow<Boolean>

    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setUserName(name: String)
    suspend fun setNotificationEnabled(enabled: Boolean)
    suspend fun setAiReminderEnabled(enabled: Boolean)
    suspend fun setPomodoroSettings(
        focus: Int, shortBreak: Int, longBreak: Int
    )
    suspend fun updateStreak(): Int?
}
