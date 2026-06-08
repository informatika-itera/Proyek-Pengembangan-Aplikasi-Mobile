package id.my.sinanonym.mybawanggacha.presentation.screens.search

import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchFilters
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchItem
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchMediaType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchPresentationFormatterTest {
    @Test
    fun defaultDropdownLabel_shouldUseContextSpecificLabels() {
        assertEquals(SearchText.anyTypeLabel, defaultDropdownLabel(SearchText.typeLabel))
        assertEquals(SearchText.anyStatusLabel, defaultDropdownLabel(SearchText.statusLabel))
        assertEquals(SearchText.anyRatingLabel, defaultDropdownLabel(SearchText.ratingLabel))
        assertEquals(SearchText.defaultOrderByLabel, defaultDropdownLabel(SearchText.orderByLabel))
        assertEquals(SearchText.defaultSortLabel, defaultDropdownLabel(SearchText.sortLabel))
    }

    @Test
    fun buildActiveFilterLabels_shouldOnlyIncludeMeaningfulNonDefaultFilters() {
        val filters = MediaSearchFilters(
            mediaType = SearchMediaType.Anime,
            query = "frieren",
            limit = "25",
            type = "tv",
            status = "airing",
            rating = "pg13",
            minScore = "8",
            maxScore = "10",
            orderBy = "score",
            sort = "desc",
            sfw = false,
            unapproved = true,
            producers = "1,2"
        )

        val labels = buildActiveFilterLabels(filters)

        assertTrue("Query: frieren" in labels)
        assertTrue("Type: tv" in labels)
        assertTrue("Status: airing" in labels)
        assertTrue("Rating: pg13" in labels)
        assertTrue("Limit: 25" in labels)
        assertTrue("Min: 8" in labels)
        assertTrue("Max: 10" in labels)
        assertTrue("Order: score" in labels)
        assertTrue("Sort: desc" in labels)
        assertTrue(SearchText.adultAllowedLabel in labels)
        assertTrue(SearchText.unapprovedActiveLabel in labels)
        assertTrue("Producers: 1,2" in labels)
    }

    @Test
    fun buildSearchSubtitle_shouldJoinAvailableMetadata() {
        val item = MediaSearchItem(
            malId = 1,
            mediaType = SearchMediaType.Anime,
            title = "Anime",
            imageUrl = null,
            type = "TV",
            status = "Airing",
            score = 8.5,
            rank = 10,
            episodes = 12,
            chapters = null,
            volumes = null
        )

        assertEquals("TV • Airing • Score 8.5 • Rank #10 • 12 eps", buildSearchSubtitle(item))
    }

    @Test
    fun buildSearchSubtitle_whenMetadataMissing_shouldShowFallbackText() {
        val item = MediaSearchItem(
            malId = 1,
            mediaType = SearchMediaType.Manga,
            title = "Manga",
            imageUrl = null,
            type = null,
            status = null,
            score = null,
            rank = null,
            episodes = null,
            chapters = null,
            volumes = null
        )

        assertEquals(SearchText.missingMetadata, buildSearchSubtitle(item))
    }
}
