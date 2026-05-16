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
import com.example.bridgebit.presentation.screens.detail.TranslationDetailViewModel // Import baru
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
}

val useCaseModule = module {
    singleOf(::GetAllHistoryUseCase)
    singleOf(::GetVaultPhrasesUseCase)
    singleOf(::SearchHistoryUseCase)
    singleOf(::SaveTranslationUseCase)
    singleOf(::DeleteTranslationUseCase)
    singleOf(::ToggleVaultStatusUseCase)
}

val viewModelModule = module {
    viewModelOf(::DashboardViewModel)
    viewModelOf(::WorkspaceViewModel)

    // ViewModel Detail sudah dihidupkan
    viewModelOf(::TranslationDetailViewModel)
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