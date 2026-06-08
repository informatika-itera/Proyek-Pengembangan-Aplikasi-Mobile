package id.my.sinanonym.mybawanggacha.data.remote.jikan.source

import id.my.sinanonym.mybawanggacha.core.coroutines.AppDispatchers
import id.my.sinanonym.mybawanggacha.data.remote.jikan.api.JikanService
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.JikanAnimeListResponse
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.JikanRecommendationsResponse
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.MangaDetailResponse
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.RelationEntryPreviewResponse
import kotlinx.coroutines.withContext

class JikanMangaRemoteDataSource(
    private val service: JikanService,
    private val dispatchers: AppDispatchers
) {
    suspend fun fetchMangaRecommendations(): JikanRecommendationsResponse =
        withContext(dispatchers.io) {
            service.fetchMangaRecommendations()
        }

    suspend fun fetchRandomManga(): MangaDetailResponse = withContext(dispatchers.io) {
        service.fetchRandomManga()
    }

    suspend fun fetchTopManga(
        page: Int,
        type: String? = null,
        filter: String? = null
    ): JikanAnimeListResponse = withContext(dispatchers.io) {
        service.fetchTopManga(page = page, type = type, filter = filter)
    }

    suspend fun fetchMangaFullDetail(id: Int): MangaDetailResponse = withContext(dispatchers.io) {
        service.fetchMangaFullDetail(id)
    }

    suspend fun fetchRelationEntryPreview(
        id: Int,
        type: String?
    ): RelationEntryPreviewResponse = withContext(dispatchers.io) {
        service.fetchRelationEntryPreview(id = id, type = type)
    }
}
