package com.example.rewind.data.remote.dto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TmdbDtoTest {

    // ==================== TmdbMovieDto.displayTitle ====================

    @Test
    fun `displayTitle should return title when present`() {
        val dto = TmdbMovieDto(id = 1, title = "Inception", name = "Some Name")
        assertEquals("Inception", dto.displayTitle)
    }

    @Test
    fun `displayTitle should return name when title is null`() {
        val dto = TmdbMovieDto(id = 1, title = null, name = "Breaking Bad")
        assertEquals("Breaking Bad", dto.displayTitle)
    }

    @Test
    fun `displayTitle should return originalTitle when title and name are null`() {
        val dto = TmdbMovieDto(id = 1, title = null, name = null, originalTitle = "Original Title")
        assertEquals("Original Title", dto.displayTitle)
    }

    @Test
    fun `displayTitle should return originalName when title, name and originalTitle are null`() {
        val dto = TmdbMovieDto(
            id = 1, title = null, name = null,
            originalTitle = null, originalName = "Original Name"
        )
        assertEquals("Original Name", dto.displayTitle)
    }

    @Test
    fun `displayTitle should return Unknown when all names are null`() {
        val dto = TmdbMovieDto(
            id = 1, title = null, name = null,
            originalTitle = null, originalName = null
        )
        assertEquals("Unknown", dto.displayTitle)
    }

    // ==================== TmdbMovieDto.displayDate ====================

    @Test
    fun `displayDate should return releaseDate when present`() {
        val dto = TmdbMovieDto(id = 1, releaseDate = "2023-07-21", firstAirDate = "2020-01-01")
        assertEquals("2023-07-21", dto.displayDate)
    }

    @Test
    fun `displayDate should return firstAirDate when releaseDate is null`() {
        val dto = TmdbMovieDto(id = 1, releaseDate = null, firstAirDate = "2020-01-01")
        assertEquals("2020-01-01", dto.displayDate)
    }

    @Test
    fun `displayDate should return null when both dates are null`() {
        val dto = TmdbMovieDto(id = 1, releaseDate = null, firstAirDate = null)
        assertNull(dto.displayDate)
    }

    // ==================== TmdbMovieDto.posterUrl ====================

    @Test
    fun `posterUrl should return full URL with default size when posterPath exists`() {
        val dto = TmdbMovieDto(id = 1, posterPath = "/abc123.jpg")
        assertEquals("https://image.tmdb.org/t/p/w500/abc123.jpg", dto.posterUrl())
    }

    @Test
    fun `posterUrl should return null when posterPath is null`() {
        val dto = TmdbMovieDto(id = 1, posterPath = null)
        assertNull(dto.posterUrl())
    }

    @Test
    fun `posterUrl should use custom size when specified`() {
        val dto = TmdbMovieDto(id = 1, posterPath = "/abc123.jpg")
        assertEquals("https://image.tmdb.org/t/p/w200/abc123.jpg", dto.posterUrl("w200"))
    }

    // ==================== TmdbMovieDto.backdropUrl ====================

    @Test
    fun `backdropUrl should return full URL with default size when backdropPath exists`() {
        val dto = TmdbMovieDto(id = 1, backdropPath = "/backdrop.jpg")
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop.jpg", dto.backdropUrl())
    }

    @Test
    fun `backdropUrl should return null when backdropPath is null`() {
        val dto = TmdbMovieDto(id = 1, backdropPath = null)
        assertNull(dto.backdropUrl())
    }

    // ==================== TmdbMovieDto.isTvSeries ====================

    @Test
    fun `isTvSeries should return true when mediaType is tv`() {
        val dto = TmdbMovieDto(id = 1, mediaType = "tv")
        assertTrue(dto.isTvSeries)
    }

    @Test
    fun `isTvSeries should return true when name is present`() {
        val dto = TmdbMovieDto(id = 1, name = "Some Series", mediaType = null)
        assertTrue(dto.isTvSeries)
    }

    @Test
    fun `isTvSeries should return false when mediaType is movie and name is null`() {
        val dto = TmdbMovieDto(id = 1, mediaType = "movie", name = null)
        assertFalse(dto.isTvSeries)
    }

    // ==================== TmdbMovieDetailDto.displayTitle ====================

    @Test
    fun `detail displayTitle should return title when present`() {
        val dto = TmdbMovieDetailDto(id = 1, title = "Detail Title", name = "Detail Name")
        assertEquals("Detail Title", dto.displayTitle)
    }

    @Test
    fun `detail displayTitle should return name when title is null`() {
        val dto = TmdbMovieDetailDto(id = 1, title = null, name = "Detail Name")
        assertEquals("Detail Name", dto.displayTitle)
    }

    @Test
    fun `detail displayTitle should return Unknown when both are null`() {
        val dto = TmdbMovieDetailDto(id = 1, title = null, name = null)
        assertEquals("Unknown", dto.displayTitle)
    }

    // ==================== TmdbMovieDetailDto.posterUrl / backdropUrl ====================

    @Test
    fun `detail posterUrl should return full URL when posterPath exists`() {
        val dto = TmdbMovieDetailDto(id = 1, posterPath = "/poster.jpg")
        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", dto.posterUrl())
    }

    @Test
    fun `detail posterUrl should return null when posterPath is null`() {
        val dto = TmdbMovieDetailDto(id = 1, posterPath = null)
        assertNull(dto.posterUrl())
    }

    @Test
    fun `detail backdropUrl should return full URL when backdropPath exists`() {
        val dto = TmdbMovieDetailDto(id = 1, backdropPath = "/backdrop.jpg")
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop.jpg", dto.backdropUrl())
    }

    @Test
    fun `detail backdropUrl should return null when backdropPath is null`() {
        val dto = TmdbMovieDetailDto(id = 1, backdropPath = null)
        assertNull(dto.backdropUrl())
    }

    // ==================== TmdbGenreMapper ====================

    @Test
    fun `fromGenreIds should return ACTION for id 28`() {
        assertEquals("ACTION", TmdbGenreMapper.fromGenreIds(listOf(28)))
    }

    @Test
    fun `fromGenreIds should return ACTION for id 10759`() {
        assertEquals("ACTION", TmdbGenreMapper.fromGenreIds(listOf(10759)))
    }

    @Test
    fun `fromGenreIds should return COMEDY for id 35`() {
        assertEquals("COMEDY", TmdbGenreMapper.fromGenreIds(listOf(35)))
    }

    @Test
    fun `fromGenreIds should return DRAMA for id 18`() {
        assertEquals("DRAMA", TmdbGenreMapper.fromGenreIds(listOf(18)))
    }

    @Test
    fun `fromGenreIds should return HORROR for id 27`() {
        assertEquals("HORROR", TmdbGenreMapper.fromGenreIds(listOf(27)))
    }

    @Test
    fun `fromGenreIds should return ROMANCE for id 10749`() {
        assertEquals("ROMANCE", TmdbGenreMapper.fromGenreIds(listOf(10749)))
    }

    @Test
    fun `fromGenreIds should return SCIFI for id 878`() {
        assertEquals("SCIFI", TmdbGenreMapper.fromGenreIds(listOf(878)))
    }

    @Test
    fun `fromGenreIds should return THRILLER for id 53`() {
        assertEquals("THRILLER", TmdbGenreMapper.fromGenreIds(listOf(53)))
    }

    @Test
    fun `fromGenreIds should return ANIMATION for id 16`() {
        assertEquals("ANIMATION", TmdbGenreMapper.fromGenreIds(listOf(16)))
    }

    @Test
    fun `fromGenreIds should return FANTASY for id 14`() {
        assertEquals("FANTASY", TmdbGenreMapper.fromGenreIds(listOf(14)))
    }

    @Test
    fun `fromGenreIds should return OTHER for unknown ids`() {
        assertEquals("OTHER", TmdbGenreMapper.fromGenreIds(listOf(9999)))
    }

    @Test
    fun `fromGenreIds should return OTHER for empty list`() {
        assertEquals("OTHER", TmdbGenreMapper.fromGenreIds(emptyList()))
    }
}
