package com.example.mybawanggacha.core.di

import com.example.mybawanggacha.data.local.source.AnimeProgressLocalDataSource
import com.example.mybawanggacha.data.local.source.MangaDetailCacheLocalDataSource
import com.example.mybawanggacha.data.local.source.AnimeDetailCacheLocalDataSource
import com.example.mybawanggacha.data.local.source.MediaPageCacheLocalDataSource
import com.example.mybawanggacha.data.local.source.RelationPreviewCacheLocalDataSource
import com.example.mybawanggacha.data.repository.ai.AIRepositoryImpl
import com.example.mybawanggacha.data.repository.anime.AnimeRepositoryImpl
import com.example.mybawanggacha.data.repository.library.LibraryRepositoryImpl
import com.example.mybawanggacha.data.repository.manga.MangaRepositoryImpl
import com.example.mybawanggacha.data.repository.note.NoteRepositoryImpl
import com.example.mybawanggacha.data.repository.search.SearchRepositoryImpl
import com.example.mybawanggacha.data.repository.settings.SettingsRepositoryImpl
import com.example.mybawanggacha.domain.ai.repository.AIRepository
import com.example.mybawanggacha.domain.anime.repository.AnimeRepository
import com.example.mybawanggacha.domain.library.repository.LibraryRepository
import com.example.mybawanggacha.domain.manga.repository.MangaRepository
import com.example.mybawanggacha.domain.note.repository.NoteRepository
import com.example.mybawanggacha.domain.search.repository.SearchRepository
import com.example.mybawanggacha.domain.settings.repository.SettingsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::AnimeDetailCacheLocalDataSource)
    singleOf(::AnimeProgressLocalDataSource)
    singleOf(::MangaDetailCacheLocalDataSource)
    singleOf(::MediaPageCacheLocalDataSource)
    singleOf(::RelationPreviewCacheLocalDataSource)
    singleOf(::NoteRepositoryImpl) bind NoteRepository::class
    singleOf(::AIRepositoryImpl) bind AIRepository::class
    singleOf(::AnimeRepositoryImpl) bind AnimeRepository::class
    singleOf(::LibraryRepositoryImpl) bind LibraryRepository::class
    singleOf(::MangaRepositoryImpl) bind MangaRepository::class
    singleOf(::SearchRepositoryImpl) bind SearchRepository::class
    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class
}
