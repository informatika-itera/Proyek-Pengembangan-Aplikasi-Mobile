package com.studyhub.domain.model

data class UserPreferences(
    val userName: String = "Pelajar",
    val isDarkMode: Boolean = false,
    val notificationEnabled: Boolean = true,
    val isAiReminderEnabled: Boolean = true,
    val pomodoroFocusDuration: Int = 25,
    val pomodoroShortBreak: Int = 5,
    val pomodoroLongBreak: Int = 15,
    val pomodoroSessionsBeforeLong: Int = 4
)
