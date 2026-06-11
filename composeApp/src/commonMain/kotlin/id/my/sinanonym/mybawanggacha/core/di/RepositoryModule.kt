package id.my.sinanonym.mybawanggacha.core.di

import id.my.sinanonym.mybawanggacha.data.local.source.AnimeProgressLocalDataSource
import id.my.sinanonym.mybawanggacha.data.local.source.MangaDetailCacheLocalDataSource
import id.my.sinanonym.mybawanggacha.data.local.source.AnimeDetailCacheLocalDataSource
import id.my.sinanonym.mybawanggacha.data.local.source.MediaPageCacheLocalDataSource
import id.my.sinanonym.mybawanggacha.data.local.source.RelationPreviewCacheLocalDataSource
import id.my.sinanonym.mybawanggacha.data.repository.ai.AIRepositoryImpl
import id.my.sinanonym.mybawanggacha.data.repository.ai.AiTokenUsageRepositoryImpl
import id.my.sinanonym.mybawanggacha.data.repository.gacha.GachaRepositoryImpl
import id.my.sinanonym.mybawanggacha.data.repository.anime.AnimeRepositoryImpl
import id.my.sinanonym.mybawanggacha.data.repository.jikan.JikanCachePolicy
import id.my.sinanonym.mybawanggacha.data.repository.jikan.JikanRequestUsageRepositoryImpl
import id.my.sinanonym.mybawanggacha.data.repository.jikan.JikanServiceStatusRepositoryImpl
import id.my.sinanonym.mybawanggacha.data.repository.jikan.SettingsJikanCachePolicy
import id.my.sinanonym.mybawanggacha.data.repository.library.LibraryRepositoryImpl
import id.my.sinanonym.mybawanggacha.data.repository.manga.MangaRepositoryImpl
import id.my.sinanonym.mybawanggacha.data.repository.note.NoteRepositoryImpl
import id.my.sinanonym.mybawanggacha.data.repository.search.SearchRepositoryImpl
import id.my.sinanonym.mybawanggacha.data.repository.settings.GitHubReleaseRepositoryImpl
import id.my.sinanonym.mybawanggacha.data.repository.settings.SettingsRepositoryImpl
import id.my.sinanonym.mybawanggacha.domain.ai.repository.AIRepository
import id.my.sinanonym.mybawanggacha.domain.gacha.repository.GachaRepository
import id.my.sinanonym.mybawanggacha.domain.anime.repository.AnimeRepository
import id.my.sinanonym.mybawanggacha.domain.library.repository.LibraryRepository
import id.my.sinanonym.mybawanggacha.domain.manga.repository.MangaRepository
import id.my.sinanonym.mybawanggacha.domain.note.repository.NoteRepository
import id.my.sinanonym.mybawanggacha.domain.search.repository.SearchRepository
import id.my.sinanonym.mybawanggacha.domain.settings.repository.AiTokenUsageRepository
import id.my.sinanonym.mybawanggacha.domain.settings.repository.GitHubReleaseRepository
import id.my.sinanonym.mybawanggacha.domain.settings.repository.JikanRequestUsageRepository
import id.my.sinanonym.mybawanggacha.domain.settings.repository.JikanServiceStatusRepository
import id.my.sinanonym.mybawanggacha.domain.settings.repository.SettingsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import id.my.sinanonym.mybawanggacha.data.repository.ai.AiChatSessionRepositoryImpl
import id.my.sinanonym.mybawanggacha.domain.ai.repository.AiChatSessionRepository

val repositoryModule = module {
    singleOf(::AnimeDetailCacheLocalDataSource)
    singleOf(::AnimeProgressLocalDataSource)
    singleOf(::MangaDetailCacheLocalDataSource)
    singleOf(::MediaPageCacheLocalDataSource)
    singleOf(::RelationPreviewCacheLocalDataSource)
    single<JikanCachePolicy> { SettingsJikanCachePolicy(get()) }
    singleOf(::JikanRequestUsageRepositoryImpl) bind JikanRequestUsageRepository::class
    singleOf(::JikanServiceStatusRepositoryImpl) bind JikanServiceStatusRepository::class
    singleOf(::GitHubReleaseRepositoryImpl) bind GitHubReleaseRepository::class
    singleOf(::AiTokenUsageRepositoryImpl) bind AiTokenUsageRepository::class
    singleOf(::NoteRepositoryImpl) bind NoteRepository::class
    singleOf(::AiChatSessionRepositoryImpl) bind AiChatSessionRepository::class
    singleOf(::AIRepositoryImpl) bind AIRepository::class
    singleOf(::GachaRepositoryImpl) bind GachaRepository::class
    singleOf(::AnimeRepositoryImpl) bind AnimeRepository::class
    singleOf(::LibraryRepositoryImpl) bind LibraryRepository::class
    singleOf(::MangaRepositoryImpl) bind MangaRepository::class
    single<SearchRepository> {
        SearchRepositoryImpl(
            remoteDataSource = get(),
            dispatchers = get(),
            mediaPageCacheLocalDataSource = get(),
            cachePolicy = get()
        )
    }
    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class
}
