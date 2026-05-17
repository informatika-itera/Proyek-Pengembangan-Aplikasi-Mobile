package com.example.mybawanggacha.data.remote.jikan.mapper

import com.example.mybawanggacha.data.remote.jikan.dto.*
import com.example.mybawanggacha.domain.anime.model.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JikanMapperTest {

    @Test
    fun toSummaryList_shouldRemoveDuplicatesBasedOnMalId() {
        val duplicateItem1 = createFakeCatalogItem(malId = 1, title = "Anime A")
        val duplicateItem2 = createFakeCatalogItem(malId = 1, title = "Anime A Duplicate")
        val uniqueItem = createFakeCatalogItem(malId = 2, title = "Anime B")
        val dtoList = listOf(duplicateItem1, duplicateItem2, uniqueItem)

        val result = dtoList.toSummaryList()

        assertEquals(2, result.size)
        assertEquals(1, result[0].malId)
        assertEquals("Anime A", result[0].title)
    }

    @Test
    fun toSummaryList_whenEnglishTitleIsBlank_shouldFallbackToDefaultTitle() {
        val itemWithBlankEnglishTitle = createFakeCatalogItem(
            malId = 1, 
            title = "Jap Title", 
            titleEnglish = ""
        )
        val itemWithNullEnglishTitle = createFakeCatalogItem(
            malId = 2, 
            title = "Jap Title 2", 
            titleEnglish = null
        )

        val result = listOf(itemWithBlankEnglishTitle, itemWithNullEnglishTitle).toSummaryList()

        assertEquals("Jap Title", result[0].title)
        assertEquals("Jap Title 2", result[1].title)
    }

    @Test
    fun toSummaryList_whenLargeImageUrlIsNull_shouldFallbackToNormalImageUrl() {
        val item = AnimeCatalogItemDto(
            mal_id = 1,
            title = "Title",
            title_english = "English",
            rank = 1,
            score = 8.5,
            rating = "PG-13",
            images = AnimeImages(
                jpg = ImageUrls(
                    large_image_url = null,
                    image_url = "https://cdn.com/normal.jpg"
                )
            )
        )

        val result = listOf(item).toSummaryList()

        assertEquals("https://cdn.com/normal.jpg", result[0].imageUrl)
    }

    @Test
    fun toDomainPage_whenHasNextPageIsTrue_shouldIncrementRequestedPage() {
        val mockResponse = JikanAnimeListResponse(
            pagination = JikanPaginationDto(has_next_page = true),
            data = listOf(createFakeCatalogItem(malId = 10))
        )
        val currentRequestedPage = 1

        val domainPage = mockResponse.toDomainPage(requestedPage = currentRequestedPage)

        assertTrue(domainPage.hasNextPage)
        assertEquals(2, domainPage.nextPage)
        assertEquals(1, domainPage.items.size)
    }

    @Test
    fun toDomainPage_whenHasNextPageIsFalseOrNull_shouldReturnNullNextPage() {
        val mockResponseFalse = JikanAnimeListResponse(
            pagination = JikanPaginationDto(has_next_page = false),
            data = emptyList()
        )
        val mockResponseNull = JikanAnimeListResponse(
            pagination = null,
            data = emptyList()
        )

        val pageFalse = mockResponseFalse.toDomainPage(requestedPage = 5)
        val pageNull = mockResponseNull.toDomainPage(requestedPage = 5)

        assertFalse(pageFalse.hasNextPage)
        assertNull(pageFalse.nextPage)

        assertFalse(pageNull.hasNextPage)
        assertNull(pageNull.nextPage)
    }

    @Test
    fun toDomain_shouldCorrectlyMapTrailerUrlHierarchy() {
        val detailWithUrl = createBaseAnimeDetailData().copy(
            trailer = AnimeTrailerDto(url = "https://trailer.com", embed_url = "https://embed.com", youtube_id = "123")
        )
        assertEquals("https://trailer.com", detailWithUrl.toDomain(emptyMap()).trailerUrl)

        val detailWithEmbed = createBaseAnimeDetailData().copy(
            trailer = AnimeTrailerDto(url = null, embed_url = "https://embed.com", youtube_id = "123")
        )
        assertEquals("https://embed.com", detailWithEmbed.toDomain(emptyMap()).trailerUrl)

        val detailWithYoutubeId = createBaseAnimeDetailData().copy(
            trailer = AnimeTrailerDto(url = null, embed_url = null, youtube_id = "xyz987")
        )
        assertEquals("https://www.youtube.com/watch?v=xyz987", detailWithYoutubeId.toDomain(emptyMap()).trailerUrl)
    }

    @Test
    fun toDomain_shouldResolveRelationsUsingCorrectPreviewKey() {
        val entryDto = AnimeRelationEntryDto(mal_id = 55, type = "Manga", name = "Test Manga", url = "url")
        val relationDto = AnimeRelationDto(relation = "Adaptation", entry = listOf(entryDto))
        
        val detailData = createBaseAnimeDetailData().copy(relations = listOf(relationDto))

        val targetPreview = AnimeRelationPreview(
            malId = 55, type = "Manga", title = "Mapped Title", imageUrl = "img", url = "url"
        )
        val previewsMap = mapOf("manga:55" to targetPreview)

        val domainDetail = detailData.toDomain(relationPreviews = previewsMap)

        val resolvedEntry = domainDetail.relations.first().entries.first()
        assertEquals(55, resolvedEntry.malId)
        assertEquals("Mapped Title", resolvedEntry.preview?.title)
    }

    @Test
    fun previewKey_shouldFormatToLowerCaseAndColonSeparatedString() {
        val entry = AnimeRelationEntryDto(mal_id = 999, type = "AnImE", name = "Name", url = "url")

        val key = entry.previewKey()

        assertEquals("anime:999", key)
    }

    @Test
    fun animeEpisodeDto_toDomain_shouldRetainWatchedState() {
        val dto = AnimeEpisodeDto(
            mal_id = 1, title = "Ep 1", title_japanese = "J1", title_romanji = "R1",
            aired = "2026-01-01", filler = false, recap = false
        )

        val domainWatched = dto.toDomain(watched = true)
        val domainUnwatched = dto.toDomain(watched = false)

        assertTrue(domainWatched.watched)
        assertFalse(domainUnwatched.watched)
        assertEquals("Ep 1", domainWatched.title)
    }

    private fun createFakeCatalogItem(
        malId: Int, 
        title: String = "Default Title",
        titleEnglish: String? = "Default English"
    ): AnimeCatalogItemDto {
        return AnimeCatalogItemDto(
            mal_id = malId,
            title = title,
            title_english = titleEnglish,
            rank = null,
            score = null,
            rating = null,
            images = null
        )
    }

    private fun createBaseAnimeDetailData(): AnimeDetailData {
        return AnimeDetailData(
            mal_id = 0,
            url = "",
            images = AnimeImages(ImageUrls("", "", "")),
            title = "",
            title_english = null,
            title_japanese = null,
            title_synonyms = emptyList(),
            type = null,
            source = null,
            episodes = null,
            status = null,
            airing = false,
            aired = null,
            duration = null,
            rating = null,
            score = null,
            scored_by = null,
            rank = null,
            popularity = null,
            members = null,
            favorites = null,
            synopsis = null,
            background = null,
            season = null,
            year = null,
            broadcast = null,
            genres = emptyList(),
            explicit_genres = emptyList(),
            themes = emptyList(),
            demographics = emptyList(),
            studios = emptyList(),
            producers = emptyList(),
            licensors = emptyList(),
            relations = emptyList(),
            theme = null,
            external = emptyList(),
            streaming = emptyList(),
            trailer = null,
            titles = emptyList()
        )
    }
}
