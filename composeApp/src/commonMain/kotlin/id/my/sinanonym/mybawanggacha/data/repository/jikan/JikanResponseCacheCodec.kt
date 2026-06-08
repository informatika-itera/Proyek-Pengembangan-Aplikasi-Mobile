package id.my.sinanonym.mybawanggacha.data.repository.jikan

import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.AnimeDetailData
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.JikanAnimeListResponse
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.JikanRecommendationsResponse
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.JikanSeasonArchiveResponse
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.MangaDetailData
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.RelationEntryPreviewDto
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.WatchEpisodesResponse
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

internal object JikanResponseCacheCodec {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun encodeAnimeList(response: JikanAnimeListResponse): String {
        return json.encodeToString(JikanAnimeListResponse.serializer(), response)
    }

    fun decodeAnimeList(value: String): JikanAnimeListResponse {
        return json.decodeFromString(JikanAnimeListResponse.serializer(), value)
    }

    fun encodeRecommendations(response: JikanRecommendationsResponse): String {
        return json.encodeToString(JikanRecommendationsResponse.serializer(), response)
    }

    fun decodeRecommendations(value: String): JikanRecommendationsResponse {
        return json.decodeFromString(JikanRecommendationsResponse.serializer(), value)
    }

    fun encodeSeasonArchive(response: JikanSeasonArchiveResponse): String {
        return json.encodeToString(JikanSeasonArchiveResponse.serializer(), response)
    }

    fun decodeSeasonArchive(value: String): JikanSeasonArchiveResponse {
        return json.decodeFromString(JikanSeasonArchiveResponse.serializer(), value)
    }

    fun encodeAnimeDetails(details: List<AnimeDetailData>): String {
        return json.encodeToString(ListSerializer(AnimeDetailData.serializer()), details)
    }

    fun decodeAnimeDetails(value: String): List<AnimeDetailData> {
        return json.decodeFromString(ListSerializer(AnimeDetailData.serializer()), value)
    }

    fun encodeMangaDetails(details: List<MangaDetailData>): String {
        return json.encodeToString(ListSerializer(MangaDetailData.serializer()), details)
    }

    fun decodeMangaDetails(value: String): List<MangaDetailData> {
        return json.decodeFromString(ListSerializer(MangaDetailData.serializer()), value)
    }

    fun encodeWatchEpisodes(response: WatchEpisodesResponse): String {
        return json.encodeToString(WatchEpisodesResponse.serializer(), response)
    }

    fun decodeWatchEpisodes(value: String): WatchEpisodesResponse {
        return json.decodeFromString(WatchEpisodesResponse.serializer(), value)
    }

    fun encodeRelationPreview(preview: RelationEntryPreviewDto): String {
        return json.encodeToString(RelationEntryPreviewDto.serializer(), preview)
    }

    fun decodeRelationPreview(value: String): RelationEntryPreviewDto {
        return json.decodeFromString(RelationEntryPreviewDto.serializer(), value)
    }
}