package com.example.neurodeck.domain.usecase

import com.example.neurodeck.domain.model.CardReviewState
import com.example.neurodeck.domain.model.CardReviewState.Companion.MIN_EASE_FACTOR
import com.example.neurodeck.domain.model.ReviewRating
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Use case untuk menghitung [CardReviewState] berikutnya berdasarkan rating user
 * pada session review yang baru saja selesai.
 *
 * Algoritma: SuperMemo SM-2 (Wozniak, 1987).
 * Spec: https://super-memory.com/english/ol/sm2.htm
 *
 * Aturan singkat:
 * 1. Hitung ease factor baru: EF' = EF + (0.1 - (5-q) * (0.08 + (5-q) * 0.02))
 *    dengan minimum 1.3.
 * 2. Kalau rating = AGAIN (q < 3):
 *    - reset repetitions ke 0
 *    - interval ke 1 hari
 *    - kartu re-enter learning queue dari awal
 * 3. Kalau rating passing (q >= 3):
 *    - n=1 (review pertama lulus)         : interval = 1 hari
 *    - n=2 (review kedua lulus)           : interval = 6 hari
 *    - n>=3 (sudah hafal beberapa kali)   : interval = previousInterval * EF
 *
 * Catatan implementasi:
 * - Use case ini PURE (no side effect, no DB call). Cocok untuk unit test.
 * - Caller (DeckRepository / StudySessionViewModel) bertanggung jawab persist hasilnya.
 * - [now] di-inject untuk testability (bisa kontrol waktu di test).
 */
class CalculateNextReviewUseCase {

    /**
     * @param current state kartu sekarang (sebelum review yang baru ini)
     * @param rating rating user untuk review yang baru saja terjadi
     * @param now waktu sekarang (default: Clock.System.now()). Override di test.
     * @return CardReviewState baru yang harus di-persist.
     */
    operator fun invoke(
        current: CardReviewState,
        rating: ReviewRating,
        now: Instant = Clock.System.now(),
    ): CardReviewState {
        val q = rating.quality

        // Step 1: hitung ease factor baru (selalu di-update, bahkan kalau gagal)
        val newEaseFactor = calculateNewEaseFactor(current.easeFactor, q)

        // Step 2: tentukan repetitions & interval berdasarkan apakah passing
        return if (!rating.isPassing) {
            // Gagal: reset repetition, kartu balik ke jadwal 1 hari
            current.copy(
                easeFactor = newEaseFactor,
                repetitions = 0,
                intervalDays = 1,
                lastReviewedAt = now,
                dueAt = now.plus(1 * 24, DateTimeUnit.HOUR),
            )
        } else {
            // Lulus: increment repetition, hitung interval baru
            val newRepetitions = current.repetitions + 1
            val newInterval = calculateNewInterval(
                repetitions = newRepetitions,
                previousInterval = current.intervalDays,
                easeFactor = newEaseFactor,
            )
            current.copy(
                easeFactor = newEaseFactor,
                repetitions = newRepetitions,
                intervalDays = newInterval,
                lastReviewedAt = now,
                dueAt = now.plus(newInterval * 24, DateTimeUnit.HOUR),
            )
        }
    }

    /**
     * Formula SM-2 untuk ease factor:
     *   EF' = EF + (0.1 - (5-q) * (0.08 + (5-q) * 0.02))
     *
     * Dengan q dalam 0..5. Hasil di-clamp minimum [MIN_EASE_FACTOR] (1.3).
     *
     * Intuisi: makin tinggi q (jawab makin mudah), EF naik sedikit.
     * Makin rendah q (susah), EF turun. Q=4 (GOOD) → EF tidak berubah.
     */
    private fun calculateNewEaseFactor(currentEF: Double, q: Int): Double {
        val delta = 0.1 - (5 - q) * (0.08 + (5 - q) * 0.02)
        val updated = currentEF + delta
        return max(MIN_EASE_FACTOR, updated)
    }

    /**
     * Tentukan interval (dalam hari) sebelum review berikutnya:
     * - n = 1 : 1 hari
     * - n = 2 : 6 hari
     * - n >= 3 : round(previousInterval * EF)
     *
     * Catatan: untuk n >= 3, interval bisa cepat membesar
     * (contoh: previousInterval=6, EF=2.5 → 15 hari → 38 hari → 95 hari → dst).
     * Ini fitur, bukan bug — kartu yang sudah dihafal tidak perlu sering muncul.
     */
    private fun calculateNewInterval(
        repetitions: Int,
        previousInterval: Int,
        easeFactor: Double,
    ): Int = when (repetitions) {
        1 -> 1
        2 -> 6
        else -> (previousInterval * easeFactor).roundToInt().coerceAtLeast(1)
    }
}