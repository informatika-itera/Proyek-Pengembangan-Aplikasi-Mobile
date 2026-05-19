package com.example.bookku.core.di

import com.example.bookku.core.network.HttpClientFactory
import com.example.bookku.core.util.DatabaseDriverFactory
import com.example.bookku.data.local.BookDatabase
import com.example.bookku.data.local.datastore.DataStoreFactory
import com.example.bookku.data.local.datastore.UserPreferences
import com.example.bookku.data.local.datastore.create
import com.example.bookku.data.remote.api.GeminiService
import com.example.bookku.data.repository.AIRepositoryImpl
import com.example.bookku.data.repository.NoteRepositoryImpl
import com.example.bookku.domain.repository.AIRepository
import com.example.bookku.domain.repository.NoteRepository
import com.example.bookku.domain.usecase.deleteBookUseCase
import com.example.bookku.domain.usecase.GenerateIdeasUseCase
import com.example.bookku.domain.usecase.GetAllNotesUseCase
import com.example.bookku.domain.usecase.ImproveWritingUseCase
import com.example.bookku.domain.usecase.SaveNoteUseCase
import com.example.bookku.domain.usecase.SearchNotesUseCase
import com.example.bookku.domain.usecase.SummarizeNoteUseCase
import com.example.bookku.presentation.screens.addbook.AddBookViewModel
import com.example.bookku.presentation.screens.ai.AIAssistantViewModel
import com.example.bookku.presentation.screens.bookdetail.BookDetailViewModel
import com.example.bookku.presentation.screens.home.HomeViewModel
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
        BookDatabase(driverFactory.createDriver())
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
    singleOf(::deleteBookUseCase)
    singleOf(::SummarizeNoteUseCase)
    singleOf(::ImproveWritingUseCase)
    singleOf(::GenerateIdeasUseCase)
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddBookViewModel)
    viewModelOf(::BookDetailViewModel)
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
