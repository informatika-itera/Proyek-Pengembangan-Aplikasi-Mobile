package com.example.Roomie.di

import com.example.Roomie.core.util.DatabaseDriverFactory
import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.data.local.datastore.DataStoreFactory
import com.example.Roomie.data.local.datastore.UserPreferences
import com.example.Roomie.data.local.datastore.create
import com.example.Roomie.data.repository.*
import com.example.Roomie.domain.repository.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    // Local Data (DataStore)
    single { get<DataStoreFactory>().create() }
    singleOf(::UserPreferences)
    
    // Database
    single { RoomieDatabase(get<DatabaseDriverFactory>().createDriver()) }
    
    // Repositories
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::BookingRepositoryImpl) bind BookingRepository::class
    singleOf(::ReportRepositoryImpl) bind ReportRepository::class
    singleOf(::FacilityRepositoryImpl) bind FacilityRepository::class
    singleOf(::AnnouncementRepositoryImpl) bind AnnouncementRepository::class
    singleOf(::NotificationRepositoryImpl) bind NotificationRepository::class
}
