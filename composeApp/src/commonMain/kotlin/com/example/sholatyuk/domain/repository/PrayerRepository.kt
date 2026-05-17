// ── PrayerRepository.kt ──────────────────────────────────
package com.example.sholatyuk.domain.repository

import com.example.sholatyuk.domain.model.PrayerTime
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface PrayerRepository {
    fun getPrayerTimeByDate(date: LocalDate): Flow<PrayerTime?>
    suspend fun fetchAndSavePrayerTime(latitude: Double, longitude: Double, date: LocalDate): Result<PrayerTime>
    suspend fun savePrayerTime(prayerTime: PrayerTime)
}
