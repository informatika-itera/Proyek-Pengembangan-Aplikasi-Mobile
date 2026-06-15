package com.studyhub.domain.model

import androidx.compose.runtime.Stable

@Stable
data class UserPreferences(
    val userName: String = "Pelajar",
    val isDarkMode: Boolean = false,
    val notificationEnabled: Boolean = true,
    val isAiReminderEnabled: Boolean = true,
    val pomodoroFocusDuration: Int = 25,
    val pomodoroShortBreak: Int = 5,
    val pomodoroLongBreak: Int = 15,
    val pomodoroSessionsBeforeLong: Int = 4,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastUsageTimestamp: Long = 0
)
