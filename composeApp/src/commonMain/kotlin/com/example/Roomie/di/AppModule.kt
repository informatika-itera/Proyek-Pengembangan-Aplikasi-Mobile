package com.example.Roomie.di

import com.example.Roomie.data.local.datastore.DataStoreFactory
import com.example.Roomie.data.local.datastore.UserPreferences
import com.example.Roomie.data.local.datastore.create
import com.example.Roomie.presentation.home.HomeViewModel
import com.example.Roomie.presentation.profile.ProfileViewModel
import com.example.Roomie.presentation.report.ReportViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    // Data Sources & Repositories
    single { get<DataStoreFactory>().create() }
    singleOf(::UserPreferences)
    
    // Database (To be implemented with RoomieDatabase)
    // single { RoomieDatabase(get<DatabaseDriverFactory>().createDriver()) }
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ReportViewModel)
}

fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + appModule + viewModelModule)
    }
}
