package com.example.rewind.core.di

import com.example.rewind.core.network.HttpClientFactory
import com.example.rewind.core.util.DatabaseDriverFactory
import com.example.rewind.data.local.RewindDatabase
import com.example.rewind.data.local.datastore.DataStoreFactory
import com.example.rewind.data.local.datastore.UserPreferences
import com.example.rewind.data.local.datastore.create
import com.example.rewind.data.remote.api.GeminiService
import com.example.rewind.data.repository.AIRepositoryImpl
import com.example.rewind.data.repository.NoteRepositoryImpl
import com.example.rewind.domain.repository.AIRepository
import com.example.rewind.domain.repository.NoteRepository
import com.example.rewind.domain.usecase.DeleteNoteUseCase
import com.example.rewind.domain.usecase.GenerateIdeasUseCase
import com.example.rewind.domain.usecase.GetAllNotesUseCase
import com.example.rewind.domain.usecase.ImproveWritingUseCase
import com.example.rewind.domain.usecase.SaveNoteUseCase
import com.example.rewind.domain.usecase.SearchNotesUseCase
import com.example.rewind.domain.usecase.SummarizeNoteUseCase
import com.example.rewind.presentation.screens.addnote.AddNoteViewModel
import com.example.rewind.presentation.screens.ai.AIAssistantViewModel
import com.example.rewind.presentation.screens.detail.NoteDetailViewModel
import com.example.rewind.presentation.screens.home.HomeViewModel
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
        RewindDatabase(driverFactory.createDriver())
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
