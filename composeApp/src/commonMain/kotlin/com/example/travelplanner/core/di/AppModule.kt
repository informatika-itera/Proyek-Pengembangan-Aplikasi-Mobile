package com.example.travelplanner.core.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
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
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 60_000
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }
}

val databaseModule = module {
    single {
        val driverFactory: com.example.travelplanner.core.util.DatabaseDriverFactory = get()
        com.example.travelplanner.data.local.TravelPlannerDatabase(driverFactory.createDriver())
    }
}

val repositoryModule = module {
    // Suntikan repositori dasar AI
    single<com.example.travelplanner.domain.repository.AIRepository> {
        com.example.travelplanner.data.repository.AIRepositoryImpl(get())
    }

    // Suntikan repositori database lokal
    single<com.example.travelplanner.domain.repository.TripRepository> {
        com.example.travelplanner.data.repository.TripRepositoryImpl(get())
    }

    single<com.example.travelplanner.domain.repository.ExpenseRepository> {
        com.example.travelplanner.data.repository.ExpenseRepositoryImpl(get())
    }

    // Shared city image service — singleton so cache is shared across all screens
    single { com.example.travelplanner.core.service.CityImageService(get()) }

    // Suntikan UseCases
    factory { com.example.travelplanner.domain.usecase.GenerateItineraryUseCase(get()) }
    factory { com.example.travelplanner.domain.usecase.ExtractExpenseUseCase(get()) }
}

val viewModelModule = module {
    factory { com.example.travelplanner.presentation.screens.home.HomeViewModel(get(), get()) }
    factory { com.example.travelplanner.presentation.screens.planner.GenerateTripViewModel(get(), get()) }
    factory { com.example.travelplanner.presentation.screens.result.TripResultViewModel(get(), get(), get(), get()) }
    factory { com.example.travelplanner.presentation.screens.expenses.ExpenseViewModel(get(), get(), get()) }
    factory { com.example.travelplanner.presentation.screens.trips.MyTripsViewModel(get(), get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            networkModule,
            databaseModule,
            repositoryModule,
            viewModelModule
        )
    }
}

fun initKoin() = initKoin {}