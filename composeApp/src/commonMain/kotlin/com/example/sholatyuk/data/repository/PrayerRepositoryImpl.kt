package com.example.sholatyuk.data.repository

import com.example.sholatyuk.data.local.SholatYukDatabase
import com.example.sholatyuk.data.local.entity.toDomain
import com.example.sholatyuk.domain.model.PrayerTime
import com.example.sholatyuk.domain.repository.PrayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate

class PrayerRepositoryImpl(
    private val database: SholatYukDatabase
) : PrayerRepository {

    private val queries = database.prayerTimeQueries

    override fun getPrayerTimeByDate(date: LocalDate): Flow<PrayerTime?> = flow {
        val result = queries.getPrayerTimeByDate(date.toString()).executeAsOneOrNull()
        emit(result?.toDomain())
    }

    override suspend fun fetchAndSavePrayerTime(
        latitude: Double,
        longitude: Double,
        date: LocalDate
    ): Result<PrayerTime> {
        // Cek cache dulu
        val cached = queries.getPrayerTimeByDate(date.toString()).executeAsOneOrNull()
        if (cached != null) {
            return Result.success(cached.toDomain())
        }

        // Belum ada di cache — kembalikan error, nanti Sprint 3 integrasikan Aladhan API
        return Result.failure(Exception("Data sholat belum tersedia. Fitur fetch API akan ditambahkan di Sprint 3."))
    }

    override suspend fun savePrayerTime(prayerTime: PrayerTime) {
        queries.insertPrayerTime(
            date      = prayerTime.date.toString(),
            fajr      = prayerTime.fajr,
            sunrise   = prayerTime.sunrise,
            dhuhr     = prayerTime.dhuhr,
            asr       = prayerTime.asr,
            maghrib   = prayerTime.maghrib,
            isha      = prayerTime.isha,
            imsak     = prayerTime.imsak,
            midnight  = prayerTime.midnight,
            latitude  = prayerTime.latitude,
            longitude = prayerTime.longitude,
            city_name = prayerTime.cityName
        )
    }
}