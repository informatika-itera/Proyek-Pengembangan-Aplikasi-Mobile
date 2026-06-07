package com.example.rewind.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MovieModelTest {

    // ==================== isFavorite ====================

    @Test
    fun `isFavorite should return true when rating is 8 or above`() {
        val movie = Movie(title = "Great Movie", rating = 9.0f)
        assertTrue(movie.isFavorite)
    }

    @Test
    fun `isFavorite should return true when rating is exactly 8`() {
        val movie = Movie(title = "Good Movie", rating = 8.0f)
        assertTrue(movie.isFavorite)
    }

    @Test
    fun `isFavorite should return false when rating is below 8`() {
        val movie = Movie(title = "OK Movie", rating = 7.9f)
        assertFalse(movie.isFavorite)
    }

    @Test
    fun `isFavorite should return false when rating is null`() {
        val movie = Movie(title = "Unrated Movie", rating = null)
        assertFalse(movie.isFavorite)
    }

    // ==================== progressPercent ====================

    @Test
    fun `progressPercent should calculate correct percentage`() {
        val movie = Movie(title = "Series", totalEpisodes = 10, watchedEpisodes = 5)
        assertEquals(50f, movie.progressPercent)
    }

    @Test
    fun `progressPercent should return 0 when totalEpisodes is null`() {
        val movie = Movie(title = "Movie", totalEpisodes = null, watchedEpisodes = 3)
        assertEquals(0f, movie.progressPercent)
    }

    @Test
    fun `progressPercent should return 0 when totalEpisodes is 0`() {
        val movie = Movie(title = "Movie", totalEpisodes = 0, watchedEpisodes = 0)
        assertEquals(0f, movie.progressPercent)
    }

    // ==================== previewPreview ====================

    @Test
    fun `previewPreview should truncate review longer than 100 characters`() {
        val longReview = "A".repeat(150)
        val movie = Movie(title = "Movie", review = longReview)
        assertEquals("A".repeat(100) + "...", movie.previewPreview)
        assertEquals(103, movie.previewPreview.length)
    }

    @Test
    fun `previewPreview should not truncate review of exactly 100 characters`() {
        val review = "B".repeat(100)
        val movie = Movie(title = "Movie", review = review)
        assertEquals(review, movie.previewPreview)
    }

    @Test
    fun `previewPreview should not truncate short review`() {
        val movie = Movie(title = "Movie", review = "Short review")
        assertEquals("Short review", movie.previewPreview)
    }

    // ==================== isEmpty ====================

    @Test
    fun `isEmpty should return true when title is blank`() {
        val movie = Movie(title = "   ")
        assertTrue(movie.isEmpty)
    }

    @Test
    fun `isEmpty should return false when title is not blank`() {
        val movie = Movie(title = "Valid Title")
        assertFalse(movie.isEmpty)
    }

    // ==================== MovieType.fromString ====================

    @Test
    fun `MovieType fromString should return MOVIE for valid MOVIE string`() {
        assertEquals(MovieType.MOVIE, MovieType.fromString("MOVIE"))
    }

    @Test
    fun `MovieType fromString should return ANIME for valid ANIME string`() {
        assertEquals(MovieType.ANIME, MovieType.fromString("ANIME"))
    }

    @Test
    fun `MovieType fromString should return MOVIE for unknown string`() {
        assertEquals(MovieType.MOVIE, MovieType.fromString("UNKNOWN"))
    }

    // ==================== WatchStatus.fromString ====================

    @Test
    fun `WatchStatus fromString should return WATCHING for valid string`() {
        assertEquals(WatchStatus.WATCHING, WatchStatus.fromString("WATCHING"))
    }

    @Test
    fun `WatchStatus fromString should return COMPLETED for valid string`() {
        assertEquals(WatchStatus.COMPLETED, WatchStatus.fromString("COMPLETED"))
    }

    @Test
    fun `WatchStatus fromString should return PLAN_TO_WATCH for unknown string`() {
        assertEquals(WatchStatus.PLAN_TO_WATCH, WatchStatus.fromString("INVALID"))
    }

    // ==================== MovieGenre.fromString ====================

    @Test
    fun `MovieGenre fromString should return ACTION for valid string`() {
        assertEquals(MovieGenre.ACTION, MovieGenre.fromString("ACTION"))
    }

    @Test
    fun `MovieGenre fromString should return HORROR for valid string`() {
        assertEquals(MovieGenre.HORROR, MovieGenre.fromString("HORROR"))
    }

    @Test
    fun `MovieGenre fromString should return OTHER for unknown string`() {
        assertEquals(MovieGenre.OTHER, MovieGenre.fromString("NONEXISTENT"))
    }
}
