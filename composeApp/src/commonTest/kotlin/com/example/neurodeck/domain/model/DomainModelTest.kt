package com.example.neurodeck.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit test untuk domain models — fokus ke logic kecil yang sering jadi
 * sumber bug halus: parsing string, default value, derived property.
 *
 * Models di sini pure (tanpa dependency framework), jadi test-nya cepat
 * dan deterministic.
 */
class DomainModelTest {

    // ==================== CardReviewState ====================

    @Test
    fun `CardReviewState default adalah kartu baru`() {
        val state = CardReviewState.initial
        assertEquals(CardReviewState.DEFAULT_EASE_FACTOR, state.easeFactor)
        assertEquals(0, state.intervalDays)
        assertEquals(0, state.repetitions)
        assertNull(state.lastReviewedAt, "Kartu baru belum pernah di-review")
    }

    @Test
    fun `isDue true ketika dueAt sudah lewat`() {
        val now = Instant.parse("2026-05-19T10:00:00Z")
        val state = CardReviewState(dueAt = now)
        assertTrue(state.isDue(now.plus(1, DateTimeUnit.HOUR)))
    }

    @Test
    fun `isDue true ketika dueAt tepat sama dengan now`() {
        val now = Instant.parse("2026-05-19T10:00:00Z")
        val state = CardReviewState(dueAt = now)
        assertTrue(state.isDue(now), "now >= dueAt harus inklusif")
    }

    @Test
    fun `isDue false ketika dueAt masih di masa depan`() {
        val now = Instant.parse("2026-05-19T10:00:00Z")
        val state = CardReviewState(dueAt = now.plus(48, DateTimeUnit.HOUR))
        assertFalse(state.isDue(now))
    }

    @Test
    fun `konstanta SM-2 sesuai spec`() {
        assertEquals(1.3, CardReviewState.MIN_EASE_FACTOR)
        assertEquals(2.5, CardReviewState.DEFAULT_EASE_FACTOR)
    }

    // ==================== ReviewRating ====================

    @Test
    fun `quality score tiap rating sesuai SM-2`() {
        assertEquals(0, ReviewRating.AGAIN.quality)
        assertEquals(3, ReviewRating.HARD.quality)
        assertEquals(4, ReviewRating.GOOD.quality)
        assertEquals(5, ReviewRating.EASY.quality)
    }

    @Test
    fun `isPassing hanya untuk rating dengan quality minimal 3`() {
        assertFalse(ReviewRating.AGAIN.isPassing, "AGAIN (q=0) tidak passing")
        assertTrue(ReviewRating.HARD.isPassing, "HARD (q=3) passing")
        assertTrue(ReviewRating.GOOD.isPassing)
        assertTrue(ReviewRating.EASY.isPassing)
    }

    // ==================== MessageRole ====================

    @Test
    fun `MessageRole fromString case-insensitive`() {
        assertEquals(MessageRole.User, MessageRole.fromString("User"))
        assertEquals(MessageRole.User, MessageRole.fromString("user"))
        assertEquals(MessageRole.Assistant, MessageRole.fromString("ASSISTANT"))
    }

    @Test
    fun `MessageRole fromString fallback ke User untuk input invalid`() {
        assertEquals(MessageRole.User, MessageRole.fromString("robot"))
        assertEquals(MessageRole.User, MessageRole.fromString(""))
    }

    // ==================== ThemeMode ====================

    @Test
    fun `ThemeMode fromName mengembalikan enum yang cocok`() {
        assertEquals(ThemeMode.Light, ThemeMode.fromName("Light"))
        assertEquals(ThemeMode.Dark, ThemeMode.fromName("Dark"))
        assertEquals(ThemeMode.System, ThemeMode.fromName("System"))
    }

    @Test
    fun `ThemeMode fromName fallback ke DEFAULT untuk null atau invalid`() {
        assertEquals(ThemeMode.DEFAULT, ThemeMode.fromName(null))
        assertEquals(ThemeMode.DEFAULT, ThemeMode.fromName("Neon"))
        assertEquals(ThemeMode.System, ThemeMode.DEFAULT)
    }

    // ==================== UserProfile ====================

    @Test
    fun `UserProfile punya default name dan username`() {
        val profile = UserProfile()
        assertEquals("Mahasiswa NeuroDeck", profile.name)
        assertEquals("@mahasiswa", profile.username)
        assertEquals("", profile.bio)
        assertNull(profile.avatarUri)
    }

    @Test
    fun `UserProfile batas panjang field sesuai konstanta`() {
        assertEquals(50, UserProfile.MAX_NAME_LENGTH)
        assertEquals(30, UserProfile.MAX_USERNAME_LENGTH)
        assertEquals(200, UserProfile.MAX_BIO_LENGTH)
    }

    // ==================== Deck ====================

    @Test
    fun `Deck baru punya cardCount 0 dan description kosong`() {
        val deck = Deck(title = "Kalkulus")
        assertEquals(0, deck.cardCount)
        assertEquals("", deck.description)
        assertEquals(0L, deck.id, "Deck belum di-persist => id 0")
    }

    // ==================== ReviewRecord ====================

    @Test
    fun `ReviewRecord menyimpan rating dan cardId`() {
        val ts: Instant = Clock.System.now()
        val record = ReviewRecord(cardId = 42L, rating = ReviewRating.GOOD, reviewedAt = ts)
        assertEquals(42L, record.cardId)
        assertEquals(ReviewRating.GOOD, record.rating)
        assertEquals(ts, record.reviewedAt)
    }
}
