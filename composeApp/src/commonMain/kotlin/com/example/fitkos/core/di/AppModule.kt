package com.example.fitkos.core.di

import com.example.fitkos.core.network.HttpClientFactory
import com.example.fitkos.core.util.DatabaseDriverFactory
import com.example.fitkos.data.local.NoteDatabase
import com.example.fitkos.data.local.datastore.DataStoreFactory
import com.example.fitkos.data.local.datastore.UserPreferences
import com.example.fitkos.data.local.datastore.create
import com.example.fitkos.data.remote.api.GeminiService
import com.example.fitkos.data.repository.AIRepositoryImpl
import com.example.fitkos.data.repository.NoteRepositoryImpl
import com.example.fitkos.data.repository.WaterRepositoryImpl
import com.example.fitkos.domain.repository.AIRepository
import com.example.fitkos.domain.repository.NoteRepository
import com.example.fitkos.domain.repository.WaterRepository
import com.example.fitkos.domain.usecase.DeleteNoteUseCase
import com.example.fitkos.domain.usecase.GenerateIdeasUseCase
import com.example.fitkos.domain.usecase.GetAllNotesUseCase
import com.example.fitkos.domain.usecase.ImproveWritingUseCase
import com.example.fitkos.domain.usecase.SaveNoteUseCase
import com.example.fitkos.domain.usecase.SearchNotesUseCase
import com.example.fitkos.domain.usecase.SummarizeNoteUseCase
import com.example.fitkos.presentation.screens.addnote.AddNoteViewModel
import com.example.fitkos.presentation.screens.ai.AIAssistantViewModel
import com.example.fitkos.presentation.screens.dashboard.DashboardViewModel
import com.example.fitkos.presentation.screens.detail.NoteDetailViewModel
import com.example.fitkos.presentation.screens.home.HomeViewModel
import com.example.fitkos.presentation.screens.settings.SettingsViewModel
import com.example.fitkos.presentation.screens.watertracker.WaterTrackerViewModel
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
    singleOf(::NoteRepositoryImpl) bind NoteRepository::class
    singleOf(::AIRepositoryImpl) bind AIRepository::class
    singleOf(::WaterRepositoryImpl) bind WaterRepository::class
}

// ==================== USE CASE MODULE ====================

val useCaseModule = module {
    singleOf(::GetAllNotesUseCase)
    singleOf(::SearchNotesUseCase)
    singleOf(::SaveNoteUseCase)
    singleOf(::DeleteNoteUseCase)
    singleOf(::SummarizeNoteUseCase)
    singleOf(::ImproveWritingUseCase)
    singleOf(::GenerateIdeasUseCase)
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddNoteViewModel)
    viewModelOf(::NoteDetailViewModel)
    viewModelOf(::AIAssistantViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::WaterTrackerViewModel)
    viewModelOf(::SettingsViewModel)
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