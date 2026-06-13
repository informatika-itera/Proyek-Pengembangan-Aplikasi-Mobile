package com.example.neurodeck.domain.reminder

/**
 * Kontrak penjadwal pengingat belajar harian (platform-agnostic).
 *
 * Implementasi:
 *   - Android: AndroidReminderScheduler (WorkManager) — di androidMain.
 *   - iOS    : belum diimplement (pakai NoOpReminderScheduler).
 *
 * Disediakan via Koin platform module, sehingga commonMain (ProfileViewModel)
 * cukup depend ke interface ini tanpa tahu detail platform.
 */
interface ReminderScheduler {

    /** Jadwalkan notifikasi harian pada jam:menit tertentu (waktu lokal). */
    fun schedule(hour: Int, minute: Int)

    /** Batalkan pengingat yang terjadwal. */
    fun cancel()
}

/**
 * Implementasi kosong — dipakai di platform yang belum mendukung notifikasi
 * (iOS) dan di unit test. Semua operasi no-op.
 */
class NoOpReminderScheduler : ReminderScheduler {
    override fun schedule(hour: Int, minute: Int) {}
    override fun cancel() {}
}