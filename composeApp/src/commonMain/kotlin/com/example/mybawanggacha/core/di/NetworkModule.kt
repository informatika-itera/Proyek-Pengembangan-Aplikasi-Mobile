package com.example.mybawanggacha.core.di

import com.example.mybawanggacha.core.network.HttpClientFactory
import com.example.mybawanggacha.data.remote.gemini.api.GeminiService
import com.example.mybawanggacha.data.remote.jikan.api.JikanService
import com.example.mybawanggacha.data.remote.jikan.source.JikanAnimeRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GeminiService)
    singleOf(::JikanService)
    singleOf(::JikanAnimeRemoteDataSource)
}
