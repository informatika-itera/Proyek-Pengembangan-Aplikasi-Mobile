package com.example.fitgen.domain.usecase

import com.example.fitgen.domain.model.MealLog
import com.example.fitgen.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

/**
 * Use case untuk menghitung total kalori semua makanan hari ini.
 */
class GetDailyCaloriesUseCase(private val repository: MealRepository) {

    operator fun invoke(): Flow<List<MealLog>> {
        val (start, end) = getTodayRange()
        return repository.getMealsByDate(start, end)
    }

    suspend fun getDailyTotal(): Int {
        val (start, end) = getTodayRange()
        return repository.getDailyCaloriesTotal(start, end)
    }

    fun calculateTotal(meals: List<MealLog>): Int = meals.sumOf { it.kalori }

    private fun getTodayRange(): Pair<Long, Long> {
        val tz    = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(tz).date
        val start = today.atStartOfDayIn(tz).toEpochMilliseconds()
        val end   = start + 24 * 60 * 60 * 1000L - 1L
        return start to end
    }
}