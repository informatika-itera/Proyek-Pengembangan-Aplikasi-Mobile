package com.example.sholatyuk.domain.usecase.prayer

import com.example.sholatyuk.domain.model.PrayerTime
import com.example.sholatyuk.domain.scheduler.AdzanScheduler

/**
 * Use case untuk menjadwalkan notifikasi adzan.
 * Dipanggil dari HomeViewModel setelah jadwal sholat berhasil diambil.
 *
 * @param adzanScheduler Platform-specific scheduler (disuntikkan oleh Koin)
 */
class ScheduleAdzanUseCase(
    private val adzanScheduler: AdzanScheduler
) {
    /**
     * Menjadwalkan semua alarm adzan berdasarkan data jadwal sholat hari ini.
     */
    suspend operator fun invoke(prayerTime: PrayerTime) {
        adzanScheduler.schedule(prayerTime)
    }
}