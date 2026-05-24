package com.example.neurodeck.domain.repository

import kotlinx.datetime.Instant

/**
 * Kontrak untuk query Review Record (history setiap session review).
 *
 * Berbeda dengan CardRepository yang fokus ke "state kartu saat ini",
 * ReviewRecordRepository fokus ke "log historis: kartu X di-review kapan
 * dengan rating apa".
 *
 * Dipakai oleh:
 *   - HomeViewModel    — stat mini (reviewedToday, streakDays)
 *   - StatsViewModel   — analytics mendalam (P4)
 *
 * Method-method di sini SEMUA suspend (one-shot) karena Home & Stats
 * butuh snapshot, bukan stream realtime (UI re-fetch saat user buka tab).
 * Kalau nanti dibutuhkan reactivity, bisa di-upgrade ke Flow.
 */
interface ReviewRecordRepository {

    /**
     * Hitung berapa kali user me-review kartu HARI INI (sejak midnight local time).
     *
     * Dipakai di Home Tab untuk stat mini "Sudah Belajar Hari Ini".
     * Counter ini reset tiap tengah malam (boundary by local timezone user).
     */
    suspend fun getReviewedToday(now: Instant): Int

    /**
     * Hitung streak harian — jumlah hari BERTURUT-TURUT user me-review minimal
     * 1 kartu. Reset ke 0 kalau ada gap (1 hari penuh tanpa review).
     *
     * Algoritma sederhana:
     *   1. Ambil timestamp review terakhir.
     *   2. Kalau review terakhir > 1 hari kalender lalu → streak = 0.
     *   3. Kalau ya hari ini atau kemarin → mulai count mundur per hari.
     *
     * Implementasi full di Repository — algoritma cukup kompleks untuk
     * diletakkan di sini sebagai dokumentasi saja.
     *
     * @param now Timestamp saat ini (di-inject untuk testability).
     * @return Jumlah hari streak (0 jika belum pernah review atau sudah putus).
     */
    suspend fun getStreakDays(now: Instant): Int

    /**
     * Total jumlah review yang pernah dilakukan user. Lifetime counter.
     * Dipakai di Profile tab achievement stats.
     */
    suspend fun getTotalReviews(): Int

    // ════════════════════════════════════════════════════════════════════════
    // STATS-SPECIFIC METHODS (P4)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Hitung total reviews dalam range periode tertentu.
     * Period start adalah midnight di local timezone, end adalah `now`.
     *
     * @param fromInclusive   Mulai timestamp (inclusive).
     * @param toExclusive     End timestamp (exclusive, biasanya `now`).
     */
    suspend fun countReviewsInRange(fromInclusive: Instant, toExclusive: Instant): Int

    /**
     * Hitung accuracy (% review yang passing rating >= 3) dalam range.
     * Return 0.0 kalau tidak ada review di range itu.
     */
    suspend fun getAccuracyInRange(fromInclusive: Instant, toExclusive: Instant): Double

    /**
     * Activity per hari dalam range (untuk bar chart).
     * Return Map<dayOffset, count>:
     *   - dayOffset 0 = hari ini
     *   - dayOffset 1 = kemarin
     *   - dayOffset 6 = 6 hari lalu (untuk 7 days view)
     *
     * @param daysBack  Berapa hari ke belakang (7 untuk weekly, 30 untuk monthly).
     * @param now       Reference time, biasanya Clock.System.now().
     */
    suspend fun getDailyActivity(daysBack: Int, now: Instant): Map<Int, Int>
}