package com.example.mybawanggacha.core.di

import com.example.mybawanggacha.core.network.HttpClientFactory
import com.example.mybawanggacha.data.remote.gemini.api.GeminiService
import com.example.mybawanggacha.data.remote.jikan.api.JikanService
import com.example.mybawanggacha.data.remote.jikan.source.JikanAnimeRemoteDataSource
import com.example.mybawanggacha.data.remote.jikan.source.JikanMangaRemoteDataSource
import com.example.mybawanggacha.data.remote.jikan.source.JikanSearchRemoteDataSource
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
    singleOf(::JikanService)
    singleOf(::JikanAnimeRemoteDataSource)
    singleOf(::JikanMangaRemoteDataSource)
    singleOf(::JikanSearchRemoteDataSource)
}
