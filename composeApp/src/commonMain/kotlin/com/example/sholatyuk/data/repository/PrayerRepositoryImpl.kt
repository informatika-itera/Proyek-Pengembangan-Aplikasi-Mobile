package com.example.sholatyuk.data.repository

import com.example.sholatyuk.data.local.SholatYukDatabase
import com.example.sholatyuk.data.local.entity.toDomain
import com.example.sholatyuk.data.remote.api.AladhanService
import com.example.sholatyuk.domain.model.PrayerTime
import com.example.sholatyuk.domain.repository.PrayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate

class PrayerRepositoryImpl(
    private val database: SholatYukDatabase,
    private val aladhanService: AladhanService
) : PrayerRepository {

    private val queries = database.prayerTimeQueries

    override fun getPrayerTimeByDate(date: LocalDate): Flow<PrayerTime?> = flow {
        val result = queries.getPrayerTimeByDate(date.toString()).executeAsList().firstOrNull()
        emit(result?.toDomain())
    }

    override suspend fun fetchAndSavePrayerTime(
        latitude: Double,
        longitude: Double,
        date: LocalDate
    ): Result<PrayerTime> {
        val cached = queries.getPrayerTimeByDate(date.toString()).executeAsList().firstOrNull()
        if (cached != null) {
            return Result.success(cached.toDomain())
        }

        return aladhanService.getTimingsByLocation(latitude, longitude).fold(
            onSuccess = { response ->
                val timings = response.data.timings
                val hijri = response.data.date.hijri
                val hijriDateStr = "${hijri.month.en} ${hijri.day}, ${hijri.year} AH"

                val newPrayerTime = PrayerTime(
                    id = 0L,
                    date = date,
                    hijriDate = hijriDateStr,
                    imsak = timings.imsak,
                    fajr = timings.fajr,
                    sunrise = timings.sunrise,
                    dhuhr = timings.dhuhr,
                    asr = timings.asr,
                    maghrib = timings.maghrib,
                    isha = timings.isha,
                    midnight = "-",
                    latitude = latitude,
                    longitude = longitude,
                    cityName = response.data.meta.timezone
                )

                savePrayerTime(newPrayerTime)
                Result.success(newPrayerTime)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
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