package com.example.mybawanggacha.data.repository

import com.example.mybawanggacha.data.local.NoteDatabase
import com.example.mybawanggacha.data.remote.api.JikanService
import com.example.mybawanggacha.data.remote.dto.AnimeDetailData
import com.example.mybawanggacha.data.remote.dto.AnimeEpisodeDto
import com.example.mybawanggacha.data.remote.dto.AnimeExternalLinkDto
import com.example.mybawanggacha.data.remote.dto.AnimeRelationEntryDto
import com.example.mybawanggacha.data.remote.dto.RelationEntryPreviewDto
import com.example.mybawanggacha.domain.model.AnimeDetail
import com.example.mybawanggacha.domain.model.AnimeDetailBundle
import com.example.mybawanggacha.domain.model.AnimeEpisode
import com.example.mybawanggacha.domain.model.AnimeLink
import com.example.mybawanggacha.domain.model.AnimeRelation
import com.example.mybawanggacha.domain.model.AnimeRelationEntry
import com.example.mybawanggacha.domain.model.AnimeRelationPreview
import com.example.mybawanggacha.domain.model.AnimeSummary
import com.example.mybawanggacha.domain.model.AnimeThemeSongs
import com.example.mybawanggacha.domain.repository.AnimeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Clock

private const val JIKAN_REQUEST_SPACING_MS = 360L

class AnimeRepositoryImpl(
    private val jikanService: JikanService,
    private val database: NoteDatabase
) : AnimeRepository {

    private val animeQueries = database.animeQueries
    private val relationPreviewCache = mutableMapOf<String, AnimeRelationPreview>()

    override suspend fun getRecommendations(): List<AnimeSummary> = withContext(Dispatchers.Default) {
        jikanService.fetchAnimeRecommendations()
            .data
            .flatMap { it.entry }
            .distinctBy { it.mal_id }
            .map { entry ->
                AnimeSummary(
                    malId = entry.mal_id,
                    title = entry.title,
                    imageUrl = entry.images.jpg.large_image_url
                )
            }
    }

    override suspend fun getAnimeDetail(malId: Int): AnimeDetailBundle = withContext(Dispatchers.Default) {
        val animeDto = jikanService.fetchAnimeFullDetail(malId).data
        val watchedNumbers = getWatchedEpisodeNumbers(malId)
        val episodeDtos = runCatching {
            jikanService.fetchAnimeEpisodes(malId).data
        }.getOrDefault(emptyList())
        val episodes = if (episodeDtos.isNotEmpty()) {
            episodeDtos.map { episode ->
                episode.toDomain(watched = episode.mal_id in watchedNumbers)
            }
        } else {
            (1..(animeDto.episodes ?: 0)).map { number ->
                AnimeEpisode(
                    number = number,
                    title = "Episode $number",
                    titleJapanese = null,
                    titleRomanji = null,
                    aired = null,
                    filler = false,
                    recap = false,
                    watched = number in watchedNumbers
                )
            }
        }

        val relationPreviews = fetchRelationPreviews(
            entries = animeDto.relations.flatMap { it.entry }
        )

        AnimeDetailBundle(
            anime = animeDto.toDomain(relationPreviews),
            episodes = episodes
        )
    }

    override suspend fun setEpisodeWatched(
        animeId: Int,
        episodeNumber: Int,
        watched: Boolean
    ) = withContext(Dispatchers.Default) {
        animeQueries.upsertEpisodeProgress(
            anime_id = animeId.toLong(),
            episode_number = episodeNumber.toLong(),
            watched = if (watched) 1L else 0L,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }

    private fun getWatchedEpisodeNumbers(animeId: Int): Set<Int> {
        return animeQueries.getWatchedEpisodeNumbers(animeId.toLong())
            .executeAsList()
            .map { it.toInt() }
            .toSet()
    }

    private suspend fun fetchRelationPreviews(
        entries: List<AnimeRelationEntryDto>
    ): Map<String, AnimeRelationPreview> {
        val previews = mutableMapOf<String, AnimeRelationPreview>()

        entries
            .distinctBy { it.previewKey() }
            .forEachIndexed { index, entry ->
                val key = entry.previewKey()
                relationPreviewCache[key]?.let { cachedPreview ->
                    previews[key] = cachedPreview
                    return@forEachIndexed
                }

                if (index > 0) delay(JIKAN_REQUEST_SPACING_MS)

                runCatching {
                    jikanService.fetchRelationEntryPreview(
                        id = entry.mal_id,
                        type = entry.type
                    ).data.toDomain()
                }.getOrNull()?.let { preview ->
                    relationPreviewCache[key] = preview
                    previews[key] = preview
                }
            }

        return previews
    }
}

private fun AnimeDetailData.toDomain(
    relationPreviews: Map<String, AnimeRelationPreview>
): AnimeDetail {
    return AnimeDetail(
        malId = mal_id,
        url = url,
        imageUrl = images.jpg.large_image_url,
        title = title,
        englishTitle = title_english,
        japaneseTitle = title_japanese,
        titleSynonyms = title_synonyms,
        type = type,
        source = source,
        episodes = episodes,
        status = status,
        airing = airing,
        aired = aired?.string,
        duration = duration,
        rating = rating,
        score = score,
        scoredBy = scored_by,
        rank = rank,
        popularity = popularity,
        members = members,
        favorites = favorites,
        synopsis = synopsis,
        background = background,
        season = season,
        year = year,
        broadcast = broadcast?.string,
        genres = genres.map { it.name },
        explicitGenres = explicit_genres.map { it.name },
        themes = themes.map { it.name },
        demographics = demographics.map { it.name },
        studios = studios.map { it.name },
        producers = producers.map { it.name },
        licensors = licensors.map { it.name },
        relations = relations.map { relation ->
            AnimeRelation(
                relation = relation.relation,
                entries = relation.entry.map { entry ->
                    entry.toDomain(preview = relationPreviews[entry.previewKey()])
                }
            )
        },
        themeSongs = AnimeThemeSongs(
            openings = theme?.openings.orEmpty(),
            endings = theme?.endings.orEmpty()
        ),
        externalLinks = external.map { it.toDomain() },
        streamingLinks = streaming.map { it.toDomain() },
        trailerUrl = trailer?.url
            ?: trailer?.embed_url
            ?: trailer?.youtube_id?.let { "https://www.youtube.com/watch?v=$it" }
    )
}

private fun AnimeEpisodeDto.toDomain(watched: Boolean): AnimeEpisode {
    return AnimeEpisode(
        number = mal_id,
        title = title,
        titleJapanese = title_japanese,
        titleRomanji = title_romanji,
        aired = aired,
        filler = filler,
        recap = recap,
        watched = watched
    )
}

private fun AnimeRelationEntryDto.toDomain(
    preview: AnimeRelationPreview?
): AnimeRelationEntry {
    return AnimeRelationEntry(
        malId = mal_id,
        type = type,
        name = name,
        url = url,
        preview = preview
    )
}

private fun RelationEntryPreviewDto.toDomain(): AnimeRelationPreview {
    return AnimeRelationPreview(
        malId = mal_id,
        type = type,
        title = title,
        imageUrl = images?.jpg?.large_image_url,
        url = url
    )
}

private fun AnimeExternalLinkDto.toDomain(): AnimeLink {
    return AnimeLink(name = name, url = url)
}

private fun AnimeRelationEntryDto.previewKey(): String {
    return "${type.orEmpty().lowercase()}:$mal_id"
}
