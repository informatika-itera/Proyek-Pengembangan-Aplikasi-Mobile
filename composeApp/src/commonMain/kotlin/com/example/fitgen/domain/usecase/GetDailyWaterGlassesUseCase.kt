package com.example.fitgen.domain.usecase

import com.example.fitgen.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class GetDailyWaterGlassesUseCase(
    private val userPreferences: UserPreferences
) {
    operator fun invoke(): Flow<Int> {
        return combine(
            userPreferences.lastHydrationDate,
            userPreferences.waterGlasses
        ) { lastDate, glasses ->
            val todayEpoch = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
            if (lastDate == todayEpoch) {
                glasses
            } else {
                0
            }
        }
    }
}
