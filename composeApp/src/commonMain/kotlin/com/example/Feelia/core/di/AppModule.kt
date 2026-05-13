package com.example.Feelia.core.di

import com.example.Feelia.core.network.HttpClientFactory
import com.example.Feelia.core.util.DatabaseDriverFactory
import com.example.Feelia.data.local.NoteDatabase
import com.example.Feelia.data.local.datastore.DataStoreFactory
import com.example.Feelia.data.local.datastore.UserPreferences
import com.example.Feelia.data.local.datastore.create
import com.example.Feelia.data.remote.api.GeminiService
import com.example.Feelia.data.repository.AIRepositoryImpl
import com.example.Feelia.data.repository.NoteRepositoryImpl
import com.example.Feelia.domain.repository.AIRepository
import com.example.Feelia.domain.repository.NoteRepository
import com.example.Feelia.domain.usecase.DeleteNoteUseCase
import com.example.Feelia.domain.usecase.GenerateIdeasUseCase
import com.example.Feelia.domain.usecase.GetAllNotesUseCase
import com.example.Feelia.domain.usecase.ImproveWritingUseCase
import com.example.Feelia.domain.usecase.SaveNoteUseCase
import com.example.Feelia.domain.usecase.SearchNotesUseCase
import com.example.Feelia.domain.usecase.SummarizeNoteUseCase
import com.example.Feelia.presentation.screens.addnote.AddNoteViewModel
import com.example.Feelia.presentation.screens.ai.AIAssistantViewModel
import com.example.Feelia.presentation.screens.detail.NoteDetailViewModel
import com.example.Feelia.presentation.screens.home.HomeViewModel
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
