package com.example.hujjah.core.di

import com.example.hujjah.core.network.HttpClientFactory
import com.example.hujjah.core.util.DatabaseDriverFactory
import com.example.hujjah.data.local.NoteDatabase
import com.example.hujjah.data.local.datastore.DataStoreFactory
import com.example.hujjah.data.local.datastore.UserPreferences
import com.example.hujjah.data.local.datastore.create
import com.example.hujjah.data.remote.api.GeminiService
import com.example.hujjah.data.repository.AIRepositoryImpl
import com.example.hujjah.data.repository.NoteRepositoryImpl
import com.example.hujjah.data.repository.hujjah.BookmarkRepositoryImpl
import com.example.hujjah.data.repository.hujjah.HujjahRepositoryImpl
import com.example.hujjah.domain.repository.AIRepository
import com.example.hujjah.domain.repository.NoteRepository
import com.example.hujjah.domain.repository.hujjah.BookmarkRepository
import com.example.hujjah.domain.repository.hujjah.HujjahRepository
import com.example.hujjah.domain.usecase.DeleteNoteUseCase
import com.example.hujjah.domain.usecase.GenerateIdeasUseCase
import com.example.hujjah.domain.usecase.GetAllNotesUseCase
import com.example.hujjah.domain.usecase.ImproveWritingUseCase
import com.example.hujjah.domain.usecase.SaveNoteUseCase
import com.example.hujjah.domain.usecase.SearchNotesUseCase
import com.example.hujjah.domain.usecase.SummarizeNoteUseCase
import com.example.hujjah.presentation.screens.addnote.AddNoteViewModel
import com.example.hujjah.presentation.screens.ai.AIAssistantViewModel
import com.example.hujjah.presentation.screens.bookmark.BookmarkViewModel
import com.example.hujjah.presentation.screens.detail.NoteDetailViewModel
import com.example.hujjah.presentation.screens.home.HomeViewModel
import com.example.hujjah.presentation.screens.lens.HujjahLensViewModel
import com.example.hujjah.presentation.screens.reference.ReferenceDetailViewModel
import com.example.hujjah.presentation.screens.result.HujjahResultViewModel
import com.example.hujjah.presentation.screens.quran.QuranViewModel
import com.example.hujjah.presentation.screens.hadith.HadithViewModel
import com.example.hujjah.presentation.screens.profile.ProfileViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
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
    // Legacy Sprint 1 repositories
    singleOf(::NoteRepositoryImpl) bind NoteRepository::class
    singleOf(::AIRepositoryImpl) bind AIRepository::class

    // Sprint 2 Hujjah repositories
    singleOf(::HujjahRepositoryImpl) bind HujjahRepository::class
    singleOf(::BookmarkRepositoryImpl) bind BookmarkRepository::class
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
    // Legacy Sprint 1 ViewModels
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddNoteViewModel)
    viewModelOf(::NoteDetailViewModel)
    viewModelOf(::AIAssistantViewModel)

    // Sprint 2 Hujjah ViewModels
    viewModelOf(::HujjahLensViewModel)
    viewModel { params -> HujjahResultViewModel(params.get(), get()) }
    viewModel { params -> ReferenceDetailViewModel(params.get(), get(), get()) }
    viewModelOf(::BookmarkViewModel)
    viewModelOf(::QuranViewModel)
    viewModelOf(::HadithViewModel)
    viewModelOf(::ProfileViewModel)
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