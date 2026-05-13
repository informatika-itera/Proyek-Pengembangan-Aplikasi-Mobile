package com.studyhub.core.di

import com.studyhub.core.network.HttpClientFactory
import com.studyhub.core.util.DatabaseDriverFactory
import com.studyhub.data.local.NoteDatabase
import com.studyhub.data.local.datastore.DataStoreFactory
import com.studyhub.data.local.datastore.UserPreferences
import com.studyhub.data.local.datastore.create
import com.studyhub.data.remote.api.GroqService
import com.studyhub.data.repository.AIRepositoryImpl
import com.studyhub.data.repository.NoteRepositoryImpl
import com.studyhub.domain.repository.AIRepository
import com.studyhub.domain.repository.NoteRepository
import com.studyhub.domain.usecase.DeleteNoteUseCase
import com.studyhub.domain.usecase.GenerateIdeasUseCase
import com.studyhub.domain.usecase.GetAllNotesUseCase
import com.studyhub.domain.usecase.ImproveWritingUseCase
import com.studyhub.domain.usecase.SaveNoteUseCase
import com.studyhub.domain.usecase.SearchNotesUseCase
import com.studyhub.domain.usecase.SummarizeNoteUseCase
import com.studyhub.presentation.screens.addnote.AddNoteViewModel
import com.studyhub.presentation.screens.ai.AIAssistantViewModel
import com.studyhub.presentation.screens.detail.NoteDetailViewModel
import com.studyhub.presentation.screens.home.HomeViewModel
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
    singleOf(::GroqService)
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
