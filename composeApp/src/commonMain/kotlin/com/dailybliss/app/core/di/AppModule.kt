package com.dailybliss.app.core.di

import com.dailybliss.app.core.network.HttpClientFactory
import com.dailybliss.app.core.util.DatabaseDriverFactory
import com.dailybliss.app.data.local.BlissDatabase
import com.dailybliss.app.data.local.datastore.DataStoreFactory
import com.dailybliss.app.data.local.datastore.UserPreferences
import com.dailybliss.app.data.local.datastore.create
import com.dailybliss.app.data.remote.api.GeminiService
import com.dailybliss.app.data.repository.AIRepositoryImpl
import com.dailybliss.app.data.repository.MomentRepositoryImpl
import com.dailybliss.app.domain.repository.AIRepository
import com.dailybliss.app.domain.repository.MomentRepository
import com.dailybliss.app.domain.usecase.DeleteMomentUseCase
import com.dailybliss.app.domain.usecase.GetAllMomentsUseCase
import com.dailybliss.app.domain.usecase.GetMomentByIdUseCase
import com.dailybliss.app.domain.usecase.SaveMomentUseCase
import com.dailybliss.app.domain.usecase.SearchMomentsUseCase
import com.dailybliss.app.presentation.screens.addnote.CreateMomentViewModel
import com.dailybliss.app.presentation.screens.ai.AIAssistantViewModel
import com.dailybliss.app.presentation.screens.detail.MomentDetailViewModel
import com.dailybliss.app.presentation.screens.home.JournalViewModel
import com.dailybliss.app.presentation.screens.home.HomeViewModel
import com.dailybliss.app.presentation.screens.settings.SettingsViewModel
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
        BlissDatabase(driverFactory.createDriver())
    }
}

// ==================== PREFERENCES MODULE ====================

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================

val repositoryModule = module {
    singleOf(::MomentRepositoryImpl) bind MomentRepository::class
    single { AIRepositoryImpl(get(), get()) } bind AIRepository::class
}

// ==================== USE CASE MODULE ====================

val useCaseModule = module {
    singleOf(::GetAllMomentsUseCase)
    singleOf(::SearchMomentsUseCase)
    singleOf(::SaveMomentUseCase)
    singleOf(::DeleteMomentUseCase)
    singleOf(::GetMomentByIdUseCase)
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::JournalViewModel)
    viewModelOf(::CreateMomentViewModel)
    viewModelOf(::MomentDetailViewModel)
    viewModelOf(::AIAssistantViewModel)
    viewModelOf(::SettingsViewModel)
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

