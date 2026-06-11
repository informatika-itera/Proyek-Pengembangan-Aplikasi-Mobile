package id.my.sinanonym.mybawanggacha.domain.manga.repository

import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaPage
import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaDetail
import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaSummary

interface MangaRepository {
    suspend fun getTopMangaPage(page: Int): MangaPage
    suspend fun getPopularMangaPage(page: Int): MangaPage
    suspend fun getRecommendations(): List<MangaSummary>
    suspend fun getRandomManga(): MangaSummary
    suspend fun getRandomMangaPicks(count: Int, forceRefresh: Boolean = false): List<MangaSummary>
    suspend fun getMangaDetail(malId: Int, forceRefresh: Boolean = false): MangaDetail
}
