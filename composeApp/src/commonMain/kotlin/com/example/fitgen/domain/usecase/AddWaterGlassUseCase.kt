package com.example.fitgen.domain.usecase

import com.example.fitgen.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AddWaterGlassUseCase(
    private val userPreferences: UserPreferences
) {
    suspend operator fun invoke() {
        val lastHydrationEpoch = userPreferences.lastHydrationDate.first()
        val currentGlasses = userPreferences.waterGlasses.first()
        
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val todayEpoch = today.toEpochDays().toLong()
        
        if (lastHydrationEpoch == todayEpoch) {
            // Same day, increment
            userPreferences.setHydration(todayEpoch, currentGlasses + 1)
        } else {
            // New day, reset to 1
            userPreferences.setHydration(todayEpoch, 1)
        }
    }
}
