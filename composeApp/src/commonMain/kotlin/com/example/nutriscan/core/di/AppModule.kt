package com.example.nutriscan.core.di

import com.example.nutriscan.core.network.HttpClientFactory
import com.example.nutriscan.core.util.DatabaseDriverFactory
import com.example.nutriscan.data.local.NutriScanDatabase
import com.example.nutriscan.data.local.datastore.DataStoreFactory
import com.example.nutriscan.data.local.datastore.UserPreferences
import com.example.nutriscan.data.local.datastore.create
import com.example.nutriscan.data.remote.api.GeminiService
import com.example.nutriscan.data.remote.api.OpenFoodFactsService
import com.example.nutriscan.data.repository.AIRepositoryImpl
import com.example.nutriscan.data.repository.ConsultationRepositoryImpl
import com.example.nutriscan.data.repository.ConsumptionRepositoryImpl
import com.example.nutriscan.data.repository.ProductRepositoryImpl
import com.example.nutriscan.data.repository.ScanHistoryRepositoryImpl
import com.example.nutriscan.data.repository.SessionRepositoryImpl
import com.example.nutriscan.data.repository.UserProfileRepositoryImpl
import com.example.nutriscan.domain.repository.AIRepository
import com.example.nutriscan.domain.repository.ConsultationRepository
import com.example.nutriscan.domain.repository.ConsumptionRepository
import com.example.nutriscan.domain.repository.ProductRepository
import com.example.nutriscan.domain.repository.ScanHistoryRepository
import com.example.nutriscan.domain.repository.SessionRepository
import com.example.nutriscan.domain.repository.UserProfileRepository
import com.example.nutriscan.domain.usecase.AnalyzeNutritionUseCase
import com.example.nutriscan.domain.usecase.DeleteUserProfileUseCase
import com.example.nutriscan.domain.usecase.GetUserProfileUseCase
import com.example.nutriscan.domain.usecase.HasUserProfileUseCase
import com.example.nutriscan.domain.usecase.SaveUserProfileUseCase
import com.example.nutriscan.domain.usecase.UpdateUserProfileUseCase
import com.example.nutriscan.presentation.screens.auth.LoginViewModel
import com.example.nutriscan.presentation.screens.consultation.ChatViewModel
import com.example.nutriscan.presentation.screens.consultation.ConsultationViewModel
import com.example.nutriscan.presentation.screens.consultation.NutritionistDashboardViewModel
import com.example.nutriscan.presentation.screens.history.HistoryViewModel
import com.example.nutriscan.presentation.screens.home.HomeViewModel
import com.example.nutriscan.presentation.screens.onboarding.OnboardingViewModel
import com.example.nutriscan.presentation.screens.profile.ProfileViewModel
import com.example.nutriscan.presentation.screens.result.ResultViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.core.context.startKoin

// ==================== NETWORK MODULE ====================

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GeminiService)
    singleOf(::OpenFoodFactsService)
}

// ==================== DATABASE MODULE ====================

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        NutriScanDatabase(driverFactory.createDriver())
    }
}

// ==================== PREFERENCES MODULE ====================

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================

val repositoryModule = module {
    singleOf(::UserProfileRepositoryImpl) bind UserProfileRepository::class
    singleOf(::ScanHistoryRepositoryImpl) bind ScanHistoryRepository::class
    singleOf(::AIRepositoryImpl)           bind AIRepository::class
    singleOf(::ProductRepositoryImpl)      bind ProductRepository::class
    singleOf(::ConsumptionRepositoryImpl)  bind ConsumptionRepository::class
    singleOf(::ConsultationRepositoryImpl) bind ConsultationRepository::class
    // SessionRepositoryImpl butuh UserProfileRepository — pastikan urutan benar
    singleOf(::SessionRepositoryImpl)      bind SessionRepository::class
}

// ==================== USE CASE MODULE ====================

val useCaseModule = module {
    singleOf(::GetUserProfileUseCase)
    singleOf(::SaveUserProfileUseCase)
    singleOf(::UpdateUserProfileUseCase)
    singleOf(::DeleteUserProfileUseCase)
    singleOf(::HasUserProfileUseCase)
    singleOf(::AnalyzeNutritionUseCase)
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    // ViewModels tanpa parameter runtime — Koin auto-inject semua dependency
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::ConsultationViewModel)
    viewModelOf(::NutritionistDashboardViewModel)

    // ResultViewModel — menerima barcode sebagai runtime parameter
    // Panggil dari screen: koinViewModel(parameters = { parametersOf(barcode) })
    viewModel { params ->
        ResultViewModel(
            barcode                 = params.get(),
            userProfileRepository   = get(),
            scanHistoryRepository   = get(),
            productRepository       = get(),
            analyzeNutritionUseCase = get(),
            aiRepository            = get(),
            consumptionRepository   = get()
        )
    }

    // ChatViewModel — menerima conversationId sebagai runtime parameter
    // Panggil dari screen: koinViewModel(parameters = { parametersOf(conversationId) })
    viewModel { params ->
        ChatViewModel(
            conversationId         = params.get(),
            consultationRepository = get(),
            sessionRepository      = get()
        )
    }
}

// ==================== ALL SHARED MODULES ====================

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