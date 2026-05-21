package com.example.Roomie.di

import com.example.Roomie.core.util.DatabaseDriverFactory
import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.data.local.datastore.DataStoreFactory
import com.example.Roomie.data.local.datastore.UserPreferences
import com.example.Roomie.data.local.datastore.create
import com.example.Roomie.data.repository.*
import com.example.Roomie.domain.repository.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    // Network
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    // Local Data (DataStore)
    single { get<DataStoreFactory>().create() }
    singleOf(::UserPreferences)
    
    // Database
    single { RoomieDatabase(get<DatabaseDriverFactory>().createDriver()) }
    
    // Repositories
    singleOf(::SupabaseAuthRepositoryImpl) bind AuthRepository::class
    singleOf(::SupabaseBookingRepositoryImpl) bind BookingRepository::class
    singleOf(::SupabaseReportRepositoryImpl) bind ReportRepository::class
    singleOf(::SupabaseFacilityRepositoryImpl) bind FacilityRepository::class
    singleOf(::AnnouncementRepositoryImpl) bind AnnouncementRepository::class
    singleOf(::NotificationRepositoryImpl) bind NotificationRepository::class
}
