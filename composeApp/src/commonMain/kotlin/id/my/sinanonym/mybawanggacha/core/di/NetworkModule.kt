package id.my.sinanonym.mybawanggacha.core.di

import id.my.sinanonym.mybawanggacha.core.network.HttpClientFactory
import id.my.sinanonym.mybawanggacha.data.remote.gemini.api.GeminiService
import id.my.sinanonym.mybawanggacha.data.remote.github.api.GitHubReleaseService
import id.my.sinanonym.mybawanggacha.data.remote.jikan.api.JikanService
import id.my.sinanonym.mybawanggacha.data.remote.jikan.source.JikanAnimeRemoteDataSource
import id.my.sinanonym.mybawanggacha.data.remote.jikan.source.JikanMangaRemoteDataSource
import id.my.sinanonym.mybawanggacha.data.remote.jikan.source.JikanSearchRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClientFactory.create(
            enableLogging = get<Boolean>(named(NETWORK_LOGGING_ENABLED_QUALIFIER))
        )
    }
    singleOf(::GeminiService)
    singleOf(::GitHubReleaseService)
    singleOf(::JikanService)
    singleOf(::JikanAnimeRemoteDataSource)
    singleOf(::JikanMangaRemoteDataSource)
    singleOf(::JikanSearchRemoteDataSource)
}
