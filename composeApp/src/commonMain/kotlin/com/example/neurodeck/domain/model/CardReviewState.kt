package com.example.neurodeck.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * State sebuah kartu dalam siklus spaced repetition SM-2.
 *
 * SM-2 (SuperMemo 2) adalah algoritma yang dikembangkan oleh Piotr Wozniak (1987)
 * untuk menjadwalkan review flashcard secara adaptif berdasarkan kesulitan kartu.
 *
 * Tiga variable kunci per kartu:
 * - [easeFactor]   : seberapa "mudah" kartu ini diingat user. Range: 1.3 (susah) - 2.5+ (mudah).
 * - [intervalDays] : berapa hari sampai kartu ini muncul lagi setelah review terakhir.
 * - [repetitions]  : berapa kali kartu ini berhasil dijawab benar berturut-turut.
 *
 * Pada saat kartu baru pertama kali dibuat (belum pernah di-review),
 * gunakan default [initial].
 */
data class CardReviewState(
    /** Faktor kemudahan kartu, minimum 1.3 (SM-2 spec). Default 2.5 untuk kartu baru. */
    val easeFactor: Double = 2.5,

    /** Berapa hari sampai review berikutnya. 0 berarti due hari ini. */
    val intervalDays: Int = 0,

    /** Jumlah jawaban "benar" berturut-turut (Good/Easy). Reset ke 0 kalau Again. */
    val repetitions: Int = 0,

    /** Kapan kartu ini terakhir di-review. null = belum pernah. */
    val lastReviewedAt: Instant? = null,

    /**
     * Kapan kartu ini due untuk review berikutnya.
     * Untuk kartu baru = sekarang (langsung due).
     */
    val dueAt: Instant = Clock.System.now(),
) {
    /**
     * Apakah kartu ini sudah due (waktu now >= dueAt)?
     * Dipakai oleh DeckRepository.getDueCards() untuk filter kartu yang harus belajar.
     */
    fun isDue(now: Instant = Clock.System.now()): Boolean = now >= dueAt

    companion object {
        /** Konstanta SM-2 spec: minimum easeFactor yang diperbolehkan. */
        const val MIN_EASE_FACTOR: Double = 1.3

        /** Konstanta SM-2 spec: ease factor default untuk kartu baru. */
        const val DEFAULT_EASE_FACTOR: Double = 2.5

        /** Helper: bikin state default untuk kartu baru. */
        val initial: CardReviewState
            get() = CardReviewState()
    }
}

/**
 * Rating yang user berikan saat menjawab kartu di Study Session.
 *
 * Mapping ke quality score SM-2 (q):
 * - AGAIN = 0 (blackout — total tidak ingat)
 * - HARD  = 3 (jawab benar tapi sangat sulit, hampir lupa)
 * - GOOD  = 4 (jawab benar dengan effort normal — default rating)
 * - EASY  = 5 (jawab benar dengan mudah, no hesitation)
 *
 * SM-2 originalnya punya quality 0-5 (6 level). Kita pakai 4 level
 * supaya UI tombol lebih simple (Anki style).
 */
enum class ReviewRating(val quality: Int) {
    AGAIN(0),
    HARD(3),
    GOOD(4),
    EASY(5),
    ;

    /** Apakah rating ini dianggap "lulus" untuk increment repetitions counter. */
    val isPassing: Boolean get() = quality >= 3

    companion object  // ← tambah ini, biar bisa di-extend di mapper
}