package com.example.bridgebit.core.di

import com.example.bridgebit.core.network.HttpClientFactory
import com.example.bridgebit.core.util.DatabaseDriverFactory
import com.example.bridgebit.data.local.BridgeBitDatabase
import com.example.bridgebit.data.local.datastore.DataStoreFactory
import com.example.bridgebit.data.local.datastore.UserPreferences
import com.example.bridgebit.data.local.datastore.create
import com.example.bridgebit.data.remote.api.GeminiService
import com.example.bridgebit.data.repository.TranslationRepositoryImpl
import com.example.bridgebit.domain.repository.TranslationRepository
import com.example.bridgebit.domain.usecase.*
import com.example.bridgebit.presentation.screens.dashboard.DashboardViewModel
import com.example.bridgebit.presentation.screens.workspace.WorkspaceViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GeminiService)
}

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        BridgeBitDatabase(driverFactory.createDriver())
    }
}

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

val repositoryModule = module {
    singleOf(::TranslationRepositoryImpl) bind TranslationRepository::class
    // AIRepository dinonaktifkan sementara untuk Sprint 2
    // singleOf(::AIRepositoryImpl) bind AIRepository::class
}

val useCaseModule = module {
    // Translation Use Cases
    singleOf(::GetAllHistoryUseCase)
    singleOf(::GetVaultPhrasesUseCase)
    singleOf(::SearchHistoryUseCase)
    singleOf(::SaveTranslationUseCase)
    singleOf(::DeleteTranslationUseCase)
    singleOf(::ToggleVaultStatusUseCase)

    // AI Use Cases (Dinonaktifkan sementara)
    // singleOf(::SummarizeTextUseCase)
    // singleOf(::ImproveWritingUseCase)
    // singleOf(::GenerateIdeasUseCase)
}

val viewModelModule = module {
    viewModelOf(::DashboardViewModel)
    viewModelOf(::WorkspaceViewModel)

    // Screen lain dinonaktifkan sementara di Sprint 2
    // viewModelOf(::TranslationDetailViewModel)
    // viewModelOf(::AIAssistantViewModel)
}

val sharedModules = listOf(
    networkModule,
    databaseModule,
    preferencesModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)

fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}