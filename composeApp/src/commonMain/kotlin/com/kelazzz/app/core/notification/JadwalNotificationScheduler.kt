package com.kelazzz.app.core.notification

import com.kelazzz.app.domain.model.Jadwal

/**
 * Menjadwalkan notifikasi lokal untuk agenda pribadi.
 *
 * Implementasi platform Android memakai AlarmManager.
 */
interface JadwalNotificationScheduler {
    suspend fun schedule(jadwal: Jadwal)
    suspend fun cancel(jadwalId: Long)
}
