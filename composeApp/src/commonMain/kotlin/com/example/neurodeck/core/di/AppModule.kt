package com.example.neurodeck.core.di

import com.example.neurodeck.core.network.HttpClientFactory
import com.example.neurodeck.core.util.DatabaseDriverFactory
import com.example.neurodeck.data.local.NeuroDeckDatabase
import com.example.neurodeck.data.local.datastore.DataStoreFactory
import com.example.neurodeck.data.local.datastore.createDataStore
import com.example.neurodeck.data.repository.CardRepositoryImpl
import com.example.neurodeck.data.repository.DeckRepositoryImpl
import com.example.neurodeck.data.repository.UserPreferencesRepositoryImpl
import com.example.neurodeck.data.repository.ChatRepositoryImpl
import com.example.neurodeck.domain.repository.CardRepository
import com.example.neurodeck.domain.repository.DeckRepository
import com.example.neurodeck.domain.repository.UserPreferencesRepository
import com.example.neurodeck.domain.repository.ChatRepository
import com.example.neurodeck.domain.usecase.CalculateNextReviewUseCase
import com.example.neurodeck.presentation.screens.decklibrary.DeckLibraryViewModel
import com.example.neurodeck.presentation.screens.editprofile.EditProfileViewModel
import com.example.neurodeck.presentation.screens.aichat.AIChatViewModel
import com.example.neurodeck.presentation.screens.stats.StatsViewModel
import com.example.neurodeck.presentation.screens.profile.ProfileViewModel
import com.example.neurodeck.presentation.screens.studysession.StudySessionViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import com.example.neurodeck.presentation.screens.cardlist.CardListViewModel
import com.example.neurodeck.presentation.screens.addcard.AddCardViewModel
import com.example.neurodeck.presentation.screens.editcard.EditCardViewModel
import com.example.neurodeck.presentation.screens.createdeck.CreateDeckViewModel
import com.example.neurodeck.presentation.screens.home.HomeViewModel
import com.example.neurodeck.presentation.screens.importgenerate.ImportGenerateViewModel
import com.example.neurodeck.data.remote.api.GeminiService
import com.example.neurodeck.data.repository.AIRepositoryImpl
import com.example.neurodeck.data.repository.ReviewRecordRepositoryImpl
import com.example.neurodeck.domain.repository.AIRepository
import com.example.neurodeck.domain.repository.ReviewRecordRepository

// ==================== NETWORK MODULE ====================

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    single {
        GeminiService(
            httpClient = get(),
            apiKey = getApiKey(),
        )
    }
}

// ==================== DATABASE MODULE ====================
// NeuroDeckDatabase di-instantiate sekali (singleton) dengan SqlDriver dari
// DatabaseDriverFactory yang di-provide platform-specific module (androidModule/iosModule).

val databaseModule = module {
    single { NeuroDeckDatabase(get<DatabaseDriverFactory>().createDriver()) }

    // DataStore<Preferences> singleton — JANGAN buat di tempat lain karena
    // DataStore tidak boleh ada 2 instance untuk file yang sama.
    single { createDataStore(get<DataStoreFactory>()) }
}

// ==================== REPOSITORY MODULE ====================

val repositoryModule = module {
    single<DeckRepository> { DeckRepositoryImpl(get()) }
    single<CardRepository> { CardRepositoryImpl(get(), get()) }
    single<AIRepository> { AIRepositoryImpl(get()) }
    single<ReviewRecordRepository> { ReviewRecordRepositoryImpl(get()) }
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl(get()) }
    single<ChatRepository> { ChatRepositoryImpl(database = get(), aiRepository = get()) }
}

// ==================== USE CASE MODULE ====================
// Use case di-register sebagai single karena stateless dan reusable.

val useCaseModule = module {
    single { CalculateNextReviewUseCase() }
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModel { DeckLibraryViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { params ->
        StudySessionViewModel(
            deckId = params.get(),
            cardRepository = get(),
        )
    }
    viewModel { params ->
        CardListViewModel(
            deckId = params.get(),
            deckRepository = get(),
            cardRepository = get(),
        )
    }
    viewModel { params ->
        AddCardViewModel(
            deckId = params.get(),
            cardRepository = get(),
        )
    }

    viewModel { params ->
        EditCardViewModel(
            cardId = params.get(),
            cardRepository = get(),
        )
    }

    // P3d additions
    viewModel { CreateDeckViewModel(deckRepository = get()) }

    viewModel { params ->
        ImportGenerateViewModel(
            deckId = params.get(),
            deckRepository = get(),
            cardRepository = get(),
            aiRepository = get(),
        )
    }

    // P3e additions
    viewModel {
        ProfileViewModel(
            userPreferencesRepository = get(),
            deckRepository = get(),
            reviewRecordRepository = get(),
        )
    }
    viewModel {
        EditProfileViewModel(userPreferencesRepository = get())
    }

    // P3f additions
    viewModel { AIChatViewModel(chatRepository = get()) }

    // P4 additions
    viewModel {
        StatsViewModel(
            deckRepository = get(),
            cardRepository = get(),
            reviewRecordRepository = get(),
        )
    }
}

// ==================== SHARED MODULES ====================

val sharedModules = listOf(
    networkModule,
    databaseModule,
    repositoryModule,
    useCaseModule,
    viewModelModule,
)

// ==================== INIT FUNCTION ====================

fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null,
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}
// ==================== PLATFORM-SPECIFIC: API KEY ====================
// API key di-load berbeda per platform:
// - Android: dari BuildConfig (di-inject saat build via local.properties)
// - iOS    : hardcoded (placeholder, akan di-implement nanti)

expect fun getApiKey(): String