package com.example.mybawanggacha.domain.manga.repository

import com.example.mybawanggacha.domain.manga.model.MangaPage
import com.example.mybawanggacha.domain.manga.model.MangaDetail
import com.example.mybawanggacha.domain.manga.model.MangaSummary

interface MangaRepository {
    suspend fun getTopMangaPage(page: Int): MangaPage
    suspend fun getPopularMangaPage(page: Int): MangaPage
    suspend fun getRecommendations(): List<MangaSummary>
    suspend fun getRandomManga(): MangaSummary
    suspend fun getRandomMangaPicks(count: Int): List<MangaSummary>
    suspend fun getMangaDetail(malId: Int, forceRefresh: Boolean = false): MangaDetail
}
