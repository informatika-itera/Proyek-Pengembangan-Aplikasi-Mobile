package com.example.Roomie.di

import com.example.Roomie.data.local.datastore.DataStoreFactory
import com.example.Roomie.data.local.datastore.UserPreferences
import com.example.Roomie.data.local.datastore.create
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    // Local Data (DataStore)
    single { get<DataStoreFactory>().create() }
    singleOf(::UserPreferences)
    
    // Remote Data (Ktor API - To be implemented)
    
    // Repositories (To be implemented)
}
