package com.example.mybawanggacha.domain.model

import kotlin.time.Clock
import kotlin.time.Instant

data class AnimeSummary(
    val malId: Int,
    val title: String,
    val imageUrl: String?
)

data class AnimeDetail(
    val malId: Int,
    val url: String?,
    val imageUrl: String?,
    val title: String,
    val englishTitle: String?,
    val japaneseTitle: String?,
    val titleSynonyms: List<String>,
    val type: String?,
    val source: String?,
    val episodes: Int?,
    val status: String?,
    val airing: Boolean?,
    val aired: String?,
    val duration: String?,
    val rating: String?,
    val score: Double?,
    val scoredBy: Int?,
    val rank: Int?,
    val popularity: Int?,
    val members: Int?,
    val favorites: Int?,
    val synopsis: String?,
    val background: String?,
    val season: String?,
    val year: Int?,
    val broadcast: String?,
    val genres: List<String>,
    val explicitGenres: List<String>,
    val themes: List<String>,
    val demographics: List<String>,
    val studios: List<String>,
    val producers: List<String>,
    val licensors: List<String>,
    val relations: List<AnimeRelation>,
    val themeSongs: AnimeThemeSongs,
    val externalLinks: List<AnimeLink>,
    val streamingLinks: List<AnimeLink>,
    val trailerUrl: String?
)

data class AnimeDetailBundle(
    val anime: AnimeDetail,
    val episodes: List<AnimeEpisode>
)

data class AnimeEpisode(
    val number: Int,
    val title: String?,
    val titleJapanese: String?,
    val titleRomanji: String?,
    val aired: String?,
    val filler: Boolean,
    val recap: Boolean,
    val watched: Boolean
)

data class AnimeRelation(
    val relation: String,
    val entries: List<AnimeRelationEntry>
)

data class AnimeRelationEntry(
    val malId: Int,
    val type: String?,
    val name: String,
    val url: String?,
    val preview: AnimeRelationPreview? = null
)

data class AnimeRelationPreview(
    val malId: Int,
    val type: String?,
    val title: String?,
    val imageUrl: String?,
    val url: String?
)

data class AnimeThemeSongs(
    val openings: List<String>,
    val endings: List<String>
)

data class AnimeLink(
    val name: String,
    val url: String
)

data class LibraryEntry(
    val id: Long = 0,
    val mediaId: Int,
    val mediaType: MediaType,
    val title: String,
    val imageUrl: String? = null,
    val status: LibraryStatus = LibraryStatus.PlanToWatch,
    val progress: UserProgress = UserProgress(),
    val userScore: UserScore? = null,
    val notes: String? = null,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)

enum class MediaType(val storageKey: String, val displayName: String) {
    Anime("ANIME", "Anime"),
    Manga("MANGA", "Manga");

    companion object {
        fun fromStorageKey(value: String?): MediaType {
            return entries.firstOrNull { it.storageKey.equals(value, ignoreCase = true) }
                ?: Anime
        }
    }
}

enum class LibraryStatus(val storageKey: String, val defaultLabel: String) {
    PlanToWatch("PLAN_TO_WATCH", "Plan"),
    Watching("WATCHING", "Watching"),
    Completed("COMPLETED", "Completed"),
    OnHold("ON_HOLD", "On Hold"),
    Dropped("DROPPED", "Dropped");

    fun labelFor(mediaType: MediaType): String {
        return when (this) {
            PlanToWatch -> if (mediaType == MediaType.Manga) "Plan to Read" else "Plan to Watch"
            Watching -> if (mediaType == MediaType.Manga) "Reading" else "Watching"
            Completed -> if (mediaType == MediaType.Manga) "Read" else "Watched"
            OnHold -> "On Hold"
            Dropped -> "Dropped"
        }
    }

    companion object {
        fun fromStorageKey(value: String?): LibraryStatus {
            return entries.firstOrNull { it.storageKey.equals(value, ignoreCase = true) }
                ?: PlanToWatch
        }
    }
}

data class UserProgress(
    val current: Int = 0,
    val total: Int? = null
) {
    init {
        require(current >= 0) { "Progress tidak boleh negatif" }
        require(total == null || total >= 0) { "Total tidak boleh negatif" }
        require(total == null || current <= total) { "Progress tidak boleh lebih besar dari total" }
    }

    fun format(): String {
        return total?.let { "$current/$it" } ?: current.toString()
    }
}

data class UserScore(val value: Int) {
    init {
        require(value in 1..10) { "Score harus berada di rentang 1 sampai 10" }
    }
}