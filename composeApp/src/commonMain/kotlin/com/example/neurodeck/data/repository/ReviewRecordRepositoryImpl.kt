package com.example.neurodeck.data.repository

import com.example.neurodeck.data.local.NeuroDeckDatabase
import com.example.neurodeck.domain.repository.ReviewRecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DateTimeUnit

/**
 * SQLDelight-backed implementation of [ReviewRecordRepository].
 *
 * Method paling kompleks: [getStreakDays] — implementasi algoritma streak
 * yang aware terhadap timezone lokal user. Lihat KDoc method untuk detail.
 */
class ReviewRecordRepositoryImpl(
    database: NeuroDeckDatabase,
) : ReviewRecordRepository {

    private val queries = database.reviewRecordQueries

    override suspend fun getReviewedToday(now: Instant): Int = withContext(Dispatchers.IO) {
        // Hitung boundary "midnight hari ini" di local timezone user.
        // Kalau user di Jakarta jam 23:30 me-review, lalu jam 00:01 cek Home,
        // counter akan reset — sesuai expectation user (hari kalender).
        val tz = TimeZone.currentSystemDefault()
        val todayMidnight = now.toLocalDateTime(tz).date.atStartOfDayMillis(tz)
        val tomorrowMidnight = todayMidnight + MILLIS_PER_DAY

        queries.countReviewsBetween(
            fromMillis = todayMidnight,
            toMillis = tomorrowMidnight,
        ).executeAsOne().toInt()
    }

    /**
     * Algoritma streak (day-based, timezone-aware):
     *
     *   1. Ambil semua timestamp review (descending).
     *   2. Convert tiap timestamp → LocalDate (di timezone user).
     *   3. Group by tanggal — dapat Set<LocalDate> unique.
     *   4. Iterate mundur dari "hari ini" atau "kemarin" (kalau hari ini
     *      belum ada review tapi kemarin ada, streak tetap valid).
     *      Setiap hari yang ada di set → count + 1.
     *      Gap pertama → break.
     *
     * Edge cases:
     *   - Belum pernah review → streak = 0
     *   - Review terakhir > 1 hari kalender lalu → streak = 0 (sudah putus)
     *   - Review pertama kali hari ini → streak = 1
     *
     * Komplesksitas: O(n) di mana n = total review records (acceptable
     * untuk sample size mahasiswa: paling banyak ribuan reviews).
     * Optimasi: bisa LIMIT query ke ~365 last reviews kalau jadi bottleneck.
     */
    override suspend fun getStreakDays(now: Instant): Int = withContext(Dispatchers.IO) {
        val tz = TimeZone.currentSystemDefault()
        val today = now.toLocalDateTime(tz).date

        // Step 1: ambil semua timestamp.
        val timestamps = queries.selectAllReviewedAt().executeAsList()
        if (timestamps.isEmpty()) return@withContext 0

        // Step 2-3: distinct LocalDate of review.
        val reviewedDates: Set<LocalDate> = timestamps
            .map { Instant.fromEpochMilliseconds(it).toLocalDateTime(tz).date }
            .toSet()

        // Step 4: starting point — kalau hari ini ada review pakai today,
        // kalau tidak tapi kemarin ada, pakai kemarin (streak masih valid).
        // Kalau kemarin pun tidak ada → streak putus = 0.
        val startDate: LocalDate = when {
            today in reviewedDates -> today
            today.minus(1, DateTimeUnit.DAY) in reviewedDates -> today.minus(1, DateTimeUnit.DAY)
            else -> return@withContext 0
        }

        // Count consecutive days backward dari startDate.
        var streak = 0
        var cursor = startDate
        while (cursor in reviewedDates) {
            streak++
            cursor = cursor.minus(1, DateTimeUnit.DAY)
        }
        streak
    }

    override suspend fun getTotalReviews(): Int = withContext(Dispatchers.IO) {
        queries.countAll().executeAsOne().toInt()
    }

    // ════════════════════════════════════════════════════════════════════════
    // STATS-SPECIFIC METHODS (P4)
    // ════════════════════════════════════════════════════════════════════════

    override suspend fun countReviewsInRange(
        fromInclusive: Instant,
        toExclusive: Instant,
    ): Int = withContext(Dispatchers.IO) {
        queries.countReviewsBetween(
            fromMillis = fromInclusive.toEpochMilliseconds(),
            toMillis = toExclusive.toEpochMilliseconds(),
        ).executeAsOne().toInt()
    }

    override suspend fun getAccuracyInRange(
        fromInclusive: Instant,
        toExclusive: Instant,
    ): Double = withContext(Dispatchers.IO) {
        val fromMillis = fromInclusive.toEpochMilliseconds()
        val toMillis = toExclusive.toEpochMilliseconds()
        val total = queries.countReviewsBetween(fromMillis, toMillis).executeAsOne()
        if (total == 0L) return@withContext 0.0
        val passing = queries.countPassingBetween(fromMillis, toMillis).executeAsOne()
        // Return value 0.0 - 1.0 (UI akan multiply 100 untuk display %)
        passing.toDouble() / total.toDouble()
    }

    override suspend fun getDailyActivity(
        daysBack: Int,
        now: Instant,
    ): Map<Int, Int> = withContext(Dispatchers.IO) {
        val tz = TimeZone.currentSystemDefault()
        val today = now.toLocalDateTime(tz).date

        // Ambil semua reviews dalam range (1 query single + grouping in-memory).
        // Alternative: N queries (1 per day) — lebih slow karena overhead query repeat.
        // In-memory grouping cocok untuk skala mahasiswa (~1000 reviews per bulan).
        val rangeStart = today.minus(daysBack.toLong() - 1, DateTimeUnit.DAY)
            .atStartOfDayIn(tz)
            .toEpochMilliseconds()
        val rangeEnd = today.plus(1, DateTimeUnit.DAY)
            .atStartOfDayIn(tz)
            .toEpochMilliseconds()

        val timestamps = queries.selectAllReviewedAt().executeAsList()
            .filter { it in rangeStart..<rangeEnd }

        // Group by day offset (0 = today, 1 = yesterday, ...)
        val grouped = timestamps.groupBy { ts ->
            val date = Instant.fromEpochMilliseconds(ts).toLocalDateTime(tz).date
            // Hitung selisih hari dari today
            (today.toEpochDays() - date.toEpochDays()).toInt()
        }

        // Build map dengan SEMUA dayOffset (termasuk yang 0 reviews)
        // supaya UI bar chart konsisten lebar/kolom.
        (0 until daysBack).associateWith { dayOffset ->
            grouped[dayOffset]?.size ?: 0
        }
    }

    private companion object {
        const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000
    }
}

/**
 * Extension helper: convert LocalDate ke epoch millis at midnight (timezone-aware).
 * Hindari boilerplate `LocalDateTime(date, LocalTime(0,0)).toInstant(tz).toEpochMilliseconds()`.
 */
private fun LocalDate.atStartOfDayMillis(tz: TimeZone): Long {
    // atStartOfDayIn() = official kotlinx-datetime helper untuk start of day.
    return this.atStartOfDayIn(tz).toEpochMilliseconds()
}