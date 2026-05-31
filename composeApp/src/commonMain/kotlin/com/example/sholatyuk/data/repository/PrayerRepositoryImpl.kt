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
    private val aladhanService: AladhanService // <-- Tambahan: Menyuntikkan API Service
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
        // 1. Cek cache: Jika jadwal hari ini sudah ada di database, gunakan itu (hemat kuota)
        val cached = queries.getPrayerTimeByDate(date.toString()).executeAsOneOrNull()
        if (cached != null) {
            return Result.success(cached.toDomain())
        }

        // 2. Jika belum ada, ambil dari internet (Aladhan API)
        return aladhanService.getTimingsByLocation(latitude, longitude).fold(
            onSuccess = { response ->
                val timings = response.data.timings

                // 3. Mapping: Ubah data dari JSON menjadi model PrayerTime aplikasi
                val newPrayerTime = PrayerTime(
                    id = 0L, // Hapus baris ini jika PrayerTime Anda tidak mewajibkan id
                    date = date,
                    imsak = timings.imsak,
                    fajr = timings.fajr,
                    sunrise = "-", // Default sementara karena di AladhanDto belum kita masukkan
                    dhuhr = timings.dhuhr,
                    asr = timings.asr,
                    maghrib = timings.maghrib,
                    isha = timings.isha,
                    midnight = "-", // Default sementara
                    latitude = latitude,
                    longitude = longitude,
                    cityName = response.data.meta.timezone // Aladhan menyediakan timezone, bisa dipakai sementara
                )

                // 4. Simpan ke database lokal
                savePrayerTime(newPrayerTime)

                // 5. Kembalikan data yang baru saja disimpan
                Result.success(newPrayerTime)
            },
            onFailure = { exception ->
                // Jika gagal (misal tidak ada internet) kembalikan error
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