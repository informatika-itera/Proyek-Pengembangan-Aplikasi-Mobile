package com.example.fitgen.domain.usecase

import com.example.fitgen.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class UpdateLoginStreakUseCase(
    private val userPreferences: UserPreferences
) {
    suspend operator fun invoke() {
        val lastLoginEpoch = userPreferences.lastLoginDate.first()
        val currentStreak = userPreferences.currentStreak.first()
        
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val todayEpoch = today.toEpochDays().toLong()
        
        if (lastLoginEpoch == 0L) {
            // First time login ever
            userPreferences.setLoginStreak(todayEpoch, 1)
            return
        }
        
        if (lastLoginEpoch == todayEpoch) {
            // Already logged in today, do nothing
            return
        }
        
        if (todayEpoch - lastLoginEpoch == 1L) {
            // Logged in yesterday, increment streak
            userPreferences.setLoginStreak(todayEpoch, currentStreak + 1)
        } else {
            // Missed a day, reset streak
            userPreferences.setLoginStreak(todayEpoch, 1)
        }
    }
}
