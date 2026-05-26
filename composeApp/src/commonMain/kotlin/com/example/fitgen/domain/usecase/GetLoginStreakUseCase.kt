package com.example.fitgen.domain.usecase

import com.example.fitgen.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow

class GetLoginStreakUseCase(
    private val userPreferences: UserPreferences
) {
    operator fun invoke(): Flow<Int> {
        return userPreferences.currentStreak
    }
}
