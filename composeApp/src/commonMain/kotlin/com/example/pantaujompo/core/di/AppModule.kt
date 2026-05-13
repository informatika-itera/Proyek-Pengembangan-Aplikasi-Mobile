package com.example.pantaujompo.core.di

import com.example.pantaujompo.core.network.HttpClientFactory
import com.example.pantaujompo.core.util.DatabaseDriverFactory
import com.example.pantaujompo.data.local.NoteDatabase // Nanti kita rename jadi ActivityDatabase
import com.example.pantaujompo.data.local.datastore.DataStoreFactory
import com.example.pantaujompo.data.local.datastore.UserPreferences
import com.example.pantaujompo.data.local.datastore.create
import com.example.pantaujompo.data.remote.api.GeminiService
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

// ==================== NETWORK MODULE ====================
val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GeminiService)
}

// ==================== DATABASE MODULE ====================
val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        NoteDatabase(driverFactory.createDriver()) // TODO: Nanti update ke ActivityDatabase
    }
}

// ==================== PREFERENCES MODULE ====================
val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================
val repositoryModule = module {
    // TODO: Daftarkan repository baru Pantau Jompo di sini nanti
    // singleOf(::ActivityRepositoryImpl) bind ActivityRepository::class
}

// ==================== USE CASE MODULE ====================
val useCaseModule = module {
    // TODO: Daftarkan use case baru di sini nanti
}

// ==================== VIEWMODEL MODULE ====================
val viewModelModule = module {
    // TODO: Daftarkan ViewModel baru di sini nanti (Contoh: DashboardViewModel)
}

// ==================== SHARED MODULES ====================
val sharedModules = listOf(
    networkModule,
    databaseModule,
    preferencesModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)

// ==================== INIT FUNCTION ====================
fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}