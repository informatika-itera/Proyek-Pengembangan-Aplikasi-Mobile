package com.example.pantaujompo.core.di

import com.example.pantaujompo.core.network.HttpClientFactory
import com.example.pantaujompo.core.util.DatabaseDriverFactory
import com.example.pantaujompo.data.local.NoteDatabase // Nanti kita rename jadi ActivityDatabase
import com.example.pantaujompo.data.local.datastore.DataStoreFactory
import com.example.pantaujompo.data.local.datastore.UserPreferences
import com.example.pantaujompo.data.local.datastore.create
import com.example.pantaujompo.data.remote.api.GeminiService
import com.example.pantaujompo.data.remote.api.WeatherService
import com.example.pantaujompo.data.remote.api.NewsService
import com.example.pantaujompo.data.repository.ActivityRepositoryImpl
import com.example.pantaujompo.domain.repository.ActivityRepository

// 🔥 IMPORT ROOM DATABASE LO DI SINI 🔥
import com.example.pantaujompo.data.local.room.AppDatabase

// Import ViewModels
import com.example.pantaujompo.presentation.screens.profil.ProfilViewModel
import com.example.pantaujompo.presentation.screens.addedit.AddEditViewModel
import com.example.pantaujompo.presentation.screens.riwayat.RiwayatViewModel
import com.example.pantaujompo.presentation.screens.home.DashboardViewModel
import com.example.pantaujompo.presentation.screens.artikel.ArtikelViewModel

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

// ==================== NETWORK MODULE ====================
val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GeminiService)
    singleOf(::WeatherService)
    singleOf(::NewsService)
}

// ==================== DATABASE MODULE ====================
val databaseModule = module {
    // SQLDelight (Database lama - masih dipertahankan untuk referensi/migrasi jika perlu)
    single {
        val driverFactory: DatabaseDriverFactory = get()
        NoteDatabase(driverFactory.createDriver())
    }

    // Mendaftarkan DAO dari Room Database ke Koin DI
    // Koin akan otomatis mencari instance AppDatabase yang telah di-inject dari platform-specific code (AndroidMain)
    single { get<AppDatabase>().riwayatDao() }
    single { get<AppDatabase>().makananDao() }
}

// ==================== PREFERENCES MODULE ====================
val preferencesModule = module {
    // Inisialisasi DataStore untuk penyimpanan preferensi lokal (Key-Value)
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================
val repositoryModule = module {
    // Mendaftarkan ActivityRepositoryImpl ke Koin DI, yang mengimplementasikan ActivityRepository
    // Menggunakan singleOf untuk membuat singleton instance secara otomatis
    singleOf(::ActivityRepositoryImpl) bind ActivityRepository::class
}

// ==================== USE CASE MODULE ====================
val useCaseModule = module {
    // TODO: Daftarkan use case baru di sini nanti
}

// ==================== VIEWMODEL MODULE ====================
val viewModelModule = module {
    // Mendaftarkan ViewModels agar tidak force close
    viewModelOf(::ProfilViewModel)
    viewModelOf(::AddEditViewModel)
    viewModelOf(::RiwayatViewModel)

    // Koin bakal pintar nyari dependencies-nya pas DashboardViewModel dipanggil
    viewModelOf(::DashboardViewModel)
    viewModelOf(::ArtikelViewModel)
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