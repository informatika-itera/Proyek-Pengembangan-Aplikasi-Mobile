package com.example.neurodeck.domain.usecase

import com.example.neurodeck.domain.model.CardReviewState
import com.example.neurodeck.domain.model.CardReviewState.Companion.MIN_EASE_FACTOR
import com.example.neurodeck.domain.model.ReviewRating
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests untuk algoritma SM-2.
 *
 * Strategi test:
 * - Tiap test isolated, pakai fixed [fixedNow] supaya hasil deterministic.
 * - Test cover semua branch: AGAIN/HARD/GOOD/EASY × first review / subsequent reviews.
 * - Plus edge case: ease factor floor, interval growth.
 *
 * Test ini WAJIB pass sebelum integrate ke repository (Sprint 2 deliverable).
 */
class CalculateNextReviewUseCaseTest {

    private val useCase = CalculateNextReviewUseCase()
    private val fixedNow = Instant.parse("2026-05-19T10:00:00Z")

    // ==================== FIRST REVIEW (kartu baru, repetitions=0) ====================

    @Test
    fun `first review with GOOD rating sets interval to 1 day`() {
        val initial = CardReviewState.initial
        val result = useCase(initial, ReviewRating.GOOD, fixedNow)

        assertEquals(1, result.repetitions, "First passing review should set repetitions=1")
        assertEquals(1, result.intervalDays, "First passing review should set interval=1 day")
        assertEquals(fixedNow, result.lastReviewedAt)
        assertEquals(fixedNow.plus(1 * 24, DateTimeUnit.HOUR), result.dueAt)
    }

    @Test
    fun `first review with EASY rating still sets interval to 1 day (SM-2 spec)`() {
        // SM-2 tidak skip interval untuk EASY di review pertama — itu fitur,
        // bukan bug. Ease factor naik, tapi interval tetap 1 hari.
        val result = useCase(CardReviewState.initial, ReviewRating.EASY, fixedNow)

        assertEquals(1, result.repetitions)
        assertEquals(1, result.intervalDays)
        assertTrue(
            result.easeFactor > CardReviewState.DEFAULT_EASE_FACTOR,
            "EASY should bump ease factor above default 2.5",
        )
    }

    @Test
    fun `first review with AGAIN keeps repetitions at 0`() {
        val result = useCase(CardReviewState.initial, ReviewRating.AGAIN, fixedNow)

        assertEquals(0, result.repetitions, "AGAIN resets repetitions to 0")
        assertEquals(1, result.intervalDays, "AGAIN schedules card 1 day later")
        assertEquals(fixedNow.plus(1 * 24, DateTimeUnit.HOUR), result.dueAt)
    }

    // ==================== SECOND REVIEW (repetitions=1) ====================

    @Test
    fun `second passing review sets interval to 6 days`() {
        val afterFirst = CardReviewState(
            repetitions = 1,
            intervalDays = 1,
            easeFactor = 2.5,
        )
        val result = useCase(afterFirst, ReviewRating.GOOD, fixedNow)

        assertEquals(2, result.repetitions)
        assertEquals(6, result.intervalDays, "Second passing review = 6 days (SM-2 spec)")
        assertEquals(fixedNow.plus(6 * 24, DateTimeUnit.HOUR), result.dueAt)
    }

    // ==================== THIRD+ REVIEW (repetitions>=2) ====================

    @Test
    fun `third review uses previousInterval times easeFactor`() {
        val afterSecond = CardReviewState(
            repetitions = 2,
            intervalDays = 6,
            easeFactor = 2.5,
        )
        val result = useCase(afterSecond, ReviewRating.GOOD, fixedNow)

        assertEquals(3, result.repetitions)
        // 6 * 2.5 = 15
        assertEquals(15, result.intervalDays)
    }

    @Test
    fun `fourth review continues geometric growth`() {
        val afterThird = CardReviewState(
            repetitions = 3,
            intervalDays = 15,
            easeFactor = 2.5,
        )
        val result = useCase(afterThird, ReviewRating.GOOD, fixedNow)

        assertEquals(4, result.repetitions)
        // 15 * 2.5 = 37.5 → round to 38
        assertEquals(38, result.intervalDays)
    }

    // ==================== EASE FACTOR ADJUSTMENT ====================

    @Test
    fun `GOOD rating keeps ease factor stable`() {
        // q=4: delta = 0.1 - 1 * (0.08 + 1*0.02) = 0.1 - 0.1 = 0
        val initial = CardReviewState(easeFactor = 2.5)
        val result = useCase(initial, ReviewRating.GOOD, fixedNow)

        assertEquals(2.5, result.easeFactor, "GOOD should not change ease factor")
    }

    @Test
    fun `EASY rating increases ease factor`() {
        // q=5: delta = 0.1 - 0 * (...) = +0.1
        val initial = CardReviewState(easeFactor = 2.5)
        val result = useCase(initial, ReviewRating.EASY, fixedNow)

        assertEquals(2.6, result.easeFactor, "EASY should bump EF by 0.1")
    }

    @Test
    fun `HARD rating decreases ease factor`() {
        // q=3: delta = 0.1 - 2 * (0.08 + 2*0.02) = 0.1 - 2*0.12 = 0.1 - 0.24 = -0.14
        val initial = CardReviewState(easeFactor = 2.5)
        val result = useCase(initial, ReviewRating.HARD, fixedNow)

        // 2.5 - 0.14 = 2.36
        assertDoubleEquals(2.36, result.easeFactor, tolerance = 0.001)
    }

    @Test
    fun `AGAIN rating drops ease factor significantly`() {
        // q=0: delta = 0.1 - 5 * (0.08 + 5*0.02) = 0.1 - 5*0.18 = 0.1 - 0.9 = -0.8
        val initial = CardReviewState(easeFactor = 2.5)
        val result = useCase(initial, ReviewRating.AGAIN, fixedNow)

        // 2.5 - 0.8 = 1.7
        assertDoubleEquals(1.7, result.easeFactor, tolerance = 0.001)
    }

    @Test
    fun `ease factor never drops below 1_3`() {
        // Start dengan EF rendah, kasih AGAIN beberapa kali
        var state = CardReviewState(easeFactor = 1.5)
        repeat(10) {
            state = useCase(state, ReviewRating.AGAIN, fixedNow)
        }

        assertTrue(
            state.easeFactor >= MIN_EASE_FACTOR,
            "EF should never go below 1.3, got ${state.easeFactor}",
        )
        assertEquals(MIN_EASE_FACTOR, state.easeFactor)
    }

    // ==================== EDGE CASES & REGRESSION ====================

    @Test
    fun `AGAIN after multiple passing reviews resets repetitions`() {
        // Simulasi: kartu sudah review 5 kali sukses, lalu user lupa
        val matureCard = CardReviewState(
            repetitions = 5,
            intervalDays = 90,
            easeFactor = 2.5,
        )
        val result = useCase(matureCard, ReviewRating.AGAIN, fixedNow)

        assertEquals(0, result.repetitions, "AGAIN must reset repetitions back to 0")
        assertEquals(1, result.intervalDays, "AGAIN must reset interval to 1 day")
        // EF turun tapi tidak hilang sepenuhnya
        assertTrue(result.easeFactor < 2.5)
        assertTrue(result.easeFactor >= MIN_EASE_FACTOR)
    }

    @Test
    fun `dueAt is set correctly relative to now`() {
        val state = CardReviewState(repetitions = 2, intervalDays = 6, easeFactor = 2.5)
        val result = useCase(state, ReviewRating.GOOD, fixedNow)

        val expectedDue = fixedNow.plus(15 * 24, DateTimeUnit.HOUR)
        assertEquals(expectedDue, result.dueAt)
    }

    @Test
    fun `lastReviewedAt is always updated to now`() {
        val state = CardReviewState(lastReviewedAt = null)
        val result = useCase(state, ReviewRating.GOOD, fixedNow)

        assertEquals(fixedNow, result.lastReviewedAt)
    }

    @Test
    fun `isDue returns true when dueAt is past`() {
        val state = CardReviewState(dueAt = fixedNow)
        val later = fixedNow.plus(1 * 24, DateTimeUnit.HOUR)

        assertTrue(state.isDue(later))
    }

    @Test
    fun `isDue returns false when dueAt is in the future`() {
        val state = CardReviewState(dueAt = fixedNow.plus(7 * 24, DateTimeUnit.HOUR))

        assertEquals(false, state.isDue(fixedNow))
    }

    // ==================== HELPERS ====================

    /**
     * Helper: assertEquals untuk Double dengan tolerance.
     * Diperlukan karena floating point arithmetic bisa beda beberapa ULP dari expected.
     */
    private fun assertDoubleEquals(
        expected: Double,
        actual: Double,
        tolerance: Double = 0.0001,
        message: String? = null,
    ) {
        val diff = abs(expected - actual)
        assertTrue(
            diff <= tolerance,
            message ?: "Expected $expected ± $tolerance but got $actual (diff=$diff)",
        )
    }
}