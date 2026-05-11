package com.example.moneyz.core.di

import com.example.moneyz.core.network.HttpClientFactory
import com.example.moneyz.core.util.DatabaseDriverFactory
import com.example.moneyz.data.local.NoteDatabase
import com.example.moneyz.data.local.datastore.DataStoreFactory
import com.example.moneyz.data.local.datastore.UserPreferences
import com.example.moneyz.data.local.datastore.create
import com.example.moneyz.data.remote.api.GeminiService
import com.example.moneyz.data.repository.AIRepositoryImpl
import com.example.moneyz.data.repository.NoteRepositoryImpl
import com.example.moneyz.domain.repository.AIRepository
import com.example.moneyz.domain.repository.NoteRepository
import com.example.moneyz.domain.usecase.DeleteNoteUseCase
import com.example.moneyz.domain.usecase.GenerateIdeasUseCase
import com.example.moneyz.domain.usecase.GetAllNotesUseCase
import com.example.moneyz.domain.usecase.ImproveWritingUseCase
import com.example.moneyz.domain.usecase.SaveNoteUseCase
import com.example.moneyz.domain.usecase.SearchNotesUseCase
import com.example.moneyz.domain.usecase.SummarizeNoteUseCase
import com.example.moneyz.presentation.screens.addnote.AddNoteViewModel
import com.example.moneyz.presentation.screens.ai.AIAssistantViewModel
import com.example.moneyz.presentation.screens.detail.NoteDetailViewModel
import com.example.moneyz.presentation.screens.home.HomeViewModel
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
