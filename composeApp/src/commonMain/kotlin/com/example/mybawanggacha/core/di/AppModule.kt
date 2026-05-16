package com.example.mybawanggacha.core.di

import com.example.mybawanggacha.core.network.HttpClientFactory
import com.example.mybawanggacha.core.util.DatabaseDriverFactory
import com.example.mybawanggacha.data.local.NoteDatabase
import com.example.mybawanggacha.data.local.datastore.DataStoreFactory
import com.example.mybawanggacha.data.local.datastore.UserPreferences
import com.example.mybawanggacha.data.local.datastore.create
import com.example.mybawanggacha.data.remote.api.GeminiService
import com.example.mybawanggacha.data.remote.api.JikanService
import com.example.mybawanggacha.data.repository.AIRepositoryImpl
import com.example.mybawanggacha.data.repository.AnimeRepositoryImpl
import com.example.mybawanggacha.data.repository.LibraryRepositoryImpl
import com.example.mybawanggacha.data.repository.NoteRepositoryImpl
import com.example.mybawanggacha.domain.repository.AIRepository
import com.example.mybawanggacha.domain.repository.AnimeRepository
import com.example.mybawanggacha.domain.repository.LibraryRepository
import com.example.mybawanggacha.domain.repository.NoteRepository
import com.example.mybawanggacha.domain.usecase.DeleteNoteUseCase
import com.example.mybawanggacha.domain.usecase.GenerateIdeasUseCase
import com.example.mybawanggacha.domain.usecase.GetAllNotesUseCase
import com.example.mybawanggacha.domain.usecase.ImproveWritingUseCase
import com.example.mybawanggacha.domain.usecase.SaveNoteUseCase
import com.example.mybawanggacha.domain.usecase.SearchNotesUseCase
import com.example.mybawanggacha.domain.usecase.SummarizeNoteUseCase
import com.example.mybawanggacha.presentation.screens.addnote.AddNoteViewModel
import com.example.mybawanggacha.presentation.screens.ai.AIAssistantViewModel
import com.example.mybawanggacha.presentation.screens.anime.AnimeDetailViewModel
import com.example.mybawanggacha.presentation.screens.anime.AnimeHomeViewModel
import com.example.mybawanggacha.presentation.screens.detail.NoteDetailViewModel
import com.example.mybawanggacha.presentation.screens.home.HomeViewModel
import com.example.mybawanggacha.presentation.screens.library.LibraryEntryEditorViewModel
import com.example.mybawanggacha.presentation.screens.library.LibraryViewModel
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
    singleOf(::JikanService)
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
    singleOf(::AnimeRepositoryImpl) bind AnimeRepository::class
    singleOf(::LibraryRepositoryImpl) bind LibraryRepository::class
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
    viewModelOf(::AnimeDetailViewModel)
    viewModelOf(::AnimeHomeViewModel)
    viewModelOf(::LibraryViewModel)
    viewModelOf(::LibraryEntryEditorViewModel)
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
