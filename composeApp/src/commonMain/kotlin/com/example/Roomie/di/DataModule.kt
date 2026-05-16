package com.example.Roomie.di

import com.example.Roomie.core.util.DatabaseDriverFactory
import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.data.local.datastore.DataStoreFactory
import com.example.Roomie.data.local.datastore.UserPreferences
import com.example.Roomie.data.local.datastore.create
import com.example.Roomie.data.repository.AuthRepositoryImpl
import com.example.Roomie.data.repository.BookingRepositoryImpl
import com.example.Roomie.data.repository.FacilityRepositoryImpl
import com.example.Roomie.data.repository.ReportRepositoryImpl
import com.example.Roomie.domain.repository.AuthRepository
import com.example.Roomie.domain.repository.BookingRepository
import com.example.Roomie.domain.repository.FacilityRepository
import com.example.Roomie.domain.repository.ReportRepository
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
    single<ReportRepository> { ReportRepositoryImpl(get()) }
    single<FacilityRepository> { FacilityRepositoryImpl(get()) }
}
