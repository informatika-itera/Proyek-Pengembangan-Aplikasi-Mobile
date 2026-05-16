package com.example.bridgebit.core.di

import com.example.bridgebit.core.network.HttpClientFactory
import com.example.bridgebit.core.util.DatabaseDriverFactory
import com.example.bridgebit.data.local.datastore.DataStoreFactory
import com.example.bridgebit.data.local.datastore.UserPreferences
import com.example.bridgebit.data.local.datastore.create
import com.example.bridgebit.data.remote.api.GeminiService
import com.example.bridgebit.data.repository.AIRepositoryImpl
import com.example.bridgebit.domain.repository.AIRepository
import com.example.bridgebit.domain.usecase.DeleteNoteUseCase
import com.example.bridgebit.domain.usecase.GenerateIdeasUseCase
import com.example.bridgebit.domain.usecase.GetAllNotesUseCase
import com.example.bridgebit.domain.usecase.ImproveWritingUseCase
import com.example.bridgebit.domain.usecase.SaveNoteUseCase
import com.example.bridgebit.domain.usecase.SearchNotesUseCase
import com.example.bridgebit.domain.usecase.SummarizeNoteUseCase
import com.example.bridgebit.presentation.screens.ai.AIAssistantViewModel
import com.example.bridgebit.presentation.screens.dashboard.HomeViewModel
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
        BridgeBitDatabase(driverFactory.createDriver()) // ✅ Ganti ke BridgeBitDatabase
    }
}

// ==================== PREFERENCES MODULE ====================

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================

val repositoryModule = module {
    // ✅ Ganti ke TranslationRepository
    singleOf(::TranslationRepositoryImpl) bind TranslationRepository::class
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
    viewModelOf(::HomeViewModel) // Atau DashboardViewModel jika sudah diganti
    viewModelOf(::WorkspaceViewModel) // ✅
    viewModelOf(::TranslationDetailViewModel) // ✅
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
