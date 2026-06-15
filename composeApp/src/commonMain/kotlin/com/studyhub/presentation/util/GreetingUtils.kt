package com.studyhub.presentation.util

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object GreetingUtils {
    fun getGreeting(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val hour = now.hour
        return when (hour) {
            in 5..11 -> "Good morning"
            in 12..14 -> "Good afternoon"
            in 15..20 -> "Good evening"
            else -> "Good night"
        }
    }

    fun getGreetingEmoji(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val hour = now.hour
        return when (hour) {
            in 5..11 -> "☀️"
            in 12..14 -> "🌤️"
            in 15..20 -> "🌆"
            else -> "🌙"
        }
    }
}
