package id.my.sinanonym.mybawanggacha.domain.gacha.model

import id.my.sinanonym.mybawanggacha.domain.library.model.MediaType
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchItem
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchMediaType
import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
enum class GachaMediaPool(val label: String) {
    Anime("Anime"),
    Manga("Manga"),
    Both("Both");

    fun searchMediaTypes(): List<SearchMediaType> {
        return when (this) {
            Anime -> listOf(SearchMediaType.Anime)
            Manga -> listOf(SearchMediaType.Manga)
            Both -> listOf(SearchMediaType.Anime, SearchMediaType.Manga)
        }
    }
}

@Serializable
enum class GachaStatusFilter(val label: String) {
    Any("Any"),
    Ongoing("Airing / Publishing"),
    Completed("Completed");

    fun searchValueFor(mediaType: SearchMediaType): String? {
        return when (this) {
            Any -> null
            Ongoing -> when (mediaType) {
                SearchMediaType.Anime -> "airing"
                SearchMediaType.Manga -> "publishing"
            }
            Completed -> "complete"
        }
    }
}

@Serializable
enum class GachaMediaFormat(
    val label: String,
    val animeValue: String? = null,
    val mangaValue: String? = null
) {
    Any("Any"),
    Tv("TV", animeValue = "tv"),
    Movie("Movie", animeValue = "movie"),
    Ova("OVA", animeValue = "ova"),
    Ona("ONA", animeValue = "ona"),
    Special("Special", animeValue = "special"),
    Music("Music", animeValue = "music"),
    Manga("Manga", mangaValue = "manga"),
    Novel("Novel", mangaValue = "novel"),
    LightNovel("Light Novel", mangaValue = "lightnovel"),
    OneShot("One-shot", mangaValue = "oneshot"),
    Doujin("Doujin", mangaValue = "doujin"),
    Manhwa("Manhwa", mangaValue = "manhwa"),
    Manhua("Manhua", mangaValue = "manhua");

    fun searchValueFor(mediaType: SearchMediaType): String? {
        return when (mediaType) {
            SearchMediaType.Anime -> animeValue
            SearchMediaType.Manga -> mangaValue
        }
    }

    fun supports(mediaType: SearchMediaType): Boolean {
        return this == Any || searchValueFor(mediaType) != null
    }

    companion object {
        fun availableFor(pool: GachaMediaPool): List<GachaMediaFormat> {
            val targetTypes = pool.searchMediaTypes()
            return entries.filter { format ->
                format == Any || targetTypes.any { mediaType -> format.supports(mediaType) }
            }
        }
    }
}

@Serializable
data class GachaPreference(
    val mediaPool: GachaMediaPool = GachaMediaPool.Both,
    val selectedGenreIds: List<Int> = emptyList(),
    val excludedGenreIds: List<Int> = emptyList(),
    val minScore: String = "",
    val status: GachaStatusFilter = GachaStatusFilter.Any,
    val format: GachaMediaFormat = GachaMediaFormat.Any,
    val includeKnownItems: Boolean = false,
    val allowNsfw: Boolean = false
)

@Serializable
data class GachaResultItem(
    val malId: Int,
    val mediaType: GachaResultMediaType,
    val title: String,
    val imageUrl: String? = null,
    val type: String? = null,
    val status: String? = null,
    val score: Double? = null,
    val rank: Int? = null,
    val episodes: Int? = null,
    val chapters: Int? = null,
    val volumes: Int? = null
) {
    fun toSearchMediaType(): SearchMediaType {
        return mediaType.searchMediaType
    }

    fun toLibraryMediaType(): MediaType {
        return mediaType.libraryMediaType
    }
}

@Serializable
enum class GachaResultMediaType(
    val label: String,
    val searchMediaType: SearchMediaType,
    val libraryMediaType: MediaType
) {
    Anime("Anime", SearchMediaType.Anime, MediaType.Anime),
    Manga("Manga", SearchMediaType.Manga, MediaType.Manga);

    companion object {
        fun fromSearchMediaType(mediaType: SearchMediaType): GachaResultMediaType {
            return when (mediaType) {
                SearchMediaType.Anime -> Anime
                SearchMediaType.Manga -> Manga
            }
        }
    }
}

@Serializable
data class GachaHistoryEntry(
    val item: GachaResultItem,
    val pickedAtEpochMillis: Long = Clock.System.now().toEpochMilliseconds()
)

internal fun MediaSearchItem.toGachaResultItem(): GachaResultItem {
    return GachaResultItem(
        malId = malId,
        mediaType = GachaResultMediaType.fromSearchMediaType(mediaType),
        title = title,
        imageUrl = imageUrl,
        type = type,
        status = status,
        score = score,
        rank = rank,
        episodes = episodes,
        chapters = chapters,
        volumes = volumes
    )
}
