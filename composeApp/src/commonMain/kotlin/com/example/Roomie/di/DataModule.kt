package com.example.Roomie.di

import com.example.Roomie.core.util.DatabaseDriverFactory
import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.data.local.datastore.DataStoreFactory
import com.example.Roomie.data.local.datastore.UserPreferences
import com.example.Roomie.data.local.datastore.create
import com.example.Roomie.data.remote.ai.GeminiService
import com.example.Roomie.data.repository.*
import com.example.Roomie.domain.repository.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    // Network
    single { Json { ignoreUnknownKeys = true; coerceInputValues = true } }
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(get<Json>())
            }
        }
    }

    // Local Data (DataStore)
    single { get<DataStoreFactory>().create() }
    singleOf(::UserPreferences)
    
    // Database
    single { RoomieDatabase(get<DatabaseDriverFactory>().createDriver()) }
    
    // AI Service
    single { GeminiService(get(), get()) }
    
    // Repositories
    single { CoroutineScope(Dispatchers.Default + SupervisorJob()) }
    singleOf(::SupabaseAuthRepositoryImpl) bind AuthRepository::class
    single<BookingRepository> { BookingRepositoryImpl(get(), get(), get(), get(), get()) }
    single<FacilityRepository> { FacilityRepositoryImpl(get(), get(), get(), get()) }
    single<ReportRepository> { ReportRepositoryImpl(get(), get(), get(), get()) }
    singleOf(::SupabaseAuditRepositoryImpl) bind AuditRepository::class
    singleOf(::AnnouncementRepositoryImpl) bind AnnouncementRepository::class
    singleOf(::NotificationRepositoryImpl) bind NotificationRepository::class
}
