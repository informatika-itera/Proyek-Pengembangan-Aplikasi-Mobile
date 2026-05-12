package com.example.noteai.core.di

import com.example.noteai.core.network.HttpClientFactory
import com.example.noteai.core.util.DatabaseDriverFactory
import com.example.noteai.data.local.NoteDatabase
import com.example.noteai.data.local.datastore.DataStoreFactory
import com.example.noteai.data.local.datastore.UserPreferences
import com.example.noteai.data.local.datastore.create
import com.example.noteai.data.remote.api.GeminiService
import com.example.noteai.data.repository.AIRepositoryImpl
import com.example.noteai.data.repository.NoteRepositoryImpl
import com.example.noteai.domain.repository.AIRepository
import com.example.noteai.domain.repository.NoteRepository
import com.example.noteai.domain.usecase.DeleteNoteUseCase
import com.example.noteai.domain.usecase.GenerateIdeasUseCase
import com.example.noteai.domain.usecase.GetAllNotesUseCase
import com.example.noteai.domain.usecase.ImproveWritingUseCase
import com.example.noteai.domain.usecase.SaveNoteUseCase
import com.example.noteai.domain.usecase.SearchNotesUseCase
import com.example.noteai.domain.usecase.SummarizeNoteUseCase
import com.example.noteai.presentation.screens.addnote.AddNoteViewModel
import com.example.noteai.presentation.screens.ai.AIAssistantViewModel
import com.example.noteai.presentation.screens.detail.NoteDetailViewModel
import com.example.noteai.presentation.screens.home.HomeViewModel
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
