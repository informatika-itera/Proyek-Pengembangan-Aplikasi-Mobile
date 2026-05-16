package com.example.rosea.core.di

import com.example.rosea.core.network.HttpClientFactory
import com.example.rosea.core.util.DatabaseDriverFactory
import com.example.rosea.data.local.NoteDatabase
import com.example.rosea.data.local.datastore.DataStoreFactory
import com.example.rosea.data.local.datastore.UserPreferences
import com.example.rosea.data.local.datastore.create
import com.example.rosea.data.remote.api.GeminiService
import com.example.rosea.data.repository.AIRepositoryImpl
import com.example.rosea.domain.repository.AIRepository

// === IMPORT REPOSITORY BARU ===
import com.example.rosea.domain.repository.ProductRepository
import com.example.rosea.data.repository.ProductRepositoryImpl
import com.example.rosea.domain.repository.CartRepository
import com.example.rosea.data.repository.CartRepositoryImpl

// Import ViewModel yang tersisa
import com.example.rosea.presentation.screens.ai.AIAssistantViewModel
// import com.example.rosea.presentation.screens.home.HomeViewModel

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
}

// ==================== DATABASE MODULE ====================

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        // Nama instance database tetap NoteDatabase sesuai bawaan generator SQLDelight
        NoteDatabase(driverFactory.createDriver())
    }
}

// ==================== PREFERENCES MODULE ====================

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================

val repositoryModule = module {
    // Injeksi antarmuka repository ROSÉA ke implementasinya
    singleOf(::ProductRepositoryImpl) bind ProductRepository::class
    singleOf(::CartRepositoryImpl) bind CartRepository::class
    singleOf(::AIRepositoryImpl) bind AIRepository::class
}

// ==================== USE CASE MODULE ====================

val useCaseModule = module {
    // KOSONG SEMENTARA
    // Semua UseCase NoteAI lama (SaveNoteUseCase, dll) sudah dihapus.
    // Kita akan menyuntikkan Repository langsung ke ViewModel untuk versi sederhana,
    // atau membuat UseCase Product baru nanti.
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    // Viewmodel AI tetap dipertahankan karena independen
    viewModelOf(::HomeViewModel)
    viewModelOf(::AIAssistantViewModel)

    // TODO: Komen sementara karena ViewModel ini masih berisi kode NoteAI lama.
    // Jika tidak di-komen, aplikasi akan merah/error saat proses Build.
    // viewModelOf(::NoteDetailViewModel)
    // viewModelOf(::AddNoteViewModel)
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