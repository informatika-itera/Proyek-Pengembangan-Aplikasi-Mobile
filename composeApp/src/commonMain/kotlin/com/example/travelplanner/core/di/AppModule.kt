package com.example.travelplanner.core.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }
}

val repositoryModule = module {
    // Suntikan repositori dasar
    single<com.example.travelplanner.domain.repository.AIRepository> {
        com.example.travelplanner.data.repository.AIRepositoryImpl(get())
    }

    // Suntikan UseCases baru yang baru saja kita rakit, Sir
    factory { com.example.travelplanner.domain.usecase.GenerateItineraryUseCase(get()) }
    factory { com.example.travelplanner.domain.usecase.ExtractExpenseUseCase(get()) }
}

val viewModelModule = module {
    // Mendaftarkan HomeViewModel menggunakan deklarasi factory khas Koin
    factory { com.example.travelplanner.presentation.screens.home.HomeViewModel() }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            networkModule,
            repositoryModule,
            viewModelModule
        )
    }
}

fun initKoin() = initKoin {}