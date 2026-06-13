package com.example.sholatyuk.domain.scheduler

import com.example.sholatyuk.domain.model.PrayerTime

/**
 * Interface untuk menjadwalkan notifikasi adzan.
 * Didefinisikan di domain layer agar platform-agnostic.
 * Implementasinya ada di Android layer (AdzanSchedulerImpl).
 */
interface AdzanScheduler {
    suspend fun schedule(prayerTime: PrayerTime)
    fun cancelAll()
}