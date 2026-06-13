package com.example.arcane.core.di

import com.example.arcane.core.network.HttpClientFactory
import com.example.arcane.core.util.DatabaseDriverFactory
import com.example.arcane.data.local.ArcaneDatabase
import com.example.arcane.data.local.datastore.DataStoreFactory
import com.example.arcane.data.local.datastore.UserPreferences
import com.example.arcane.data.local.datastore.create
import com.example.arcane.data.remote.api.GeminiService
import com.example.arcane.data.remote.api.GoogleBooksService
import com.example.arcane.data.repository.AIRepositoryImpl
import com.example.arcane.data.repository.BookRepositoryImpl
import com.example.arcane.data.repository.FolderRepositoryImpl
import com.example.arcane.domain.repository.AIRepository
import com.example.arcane.domain.repository.BookRepository
import com.example.arcane.domain.repository.FolderRepository
import com.example.arcane.domain.usecase.BookUseCases
import com.example.arcane.domain.usecase.DeleteBookUseCase
import com.example.arcane.domain.usecase.GetBookshelfUseCase
import com.example.arcane.domain.usecase.SaveBookUseCase
import com.example.arcane.domain.usecase.SearchBooksUseCase
import com.example.arcane.domain.usecase.UpdateBookNotesUseCase
import com.example.arcane.domain.usecase.UpdateBookStatusUseCase
import com.example.arcane.presentation.screens.home.HomeViewModel
import com.example.arcane.presentation.screens.explore.ExploreViewModel
import com.example.arcane.presentation.screens.bookdetail.BookDetailViewModel
import com.example.arcane.presentation.screens.ai.AIAssistantViewModel
import com.example.arcane.presentation.screens.settings.SettingsViewModel
import com.example.arcane.presentation.screens.folder.FolderDetailViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import com.example.arcane.presentation.screens.letterbox.LetterboxViewModel

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GoogleBooksService)
    singleOf(::GeminiService)
}

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        ArcaneDatabase(driverFactory.createDriver())
    }
}

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

val repositoryModule = module {
    singleOf(::BookRepositoryImpl) bind BookRepository::class
    singleOf(::AIRepositoryImpl) bind AIRepository::class
    singleOf(::FolderRepositoryImpl) bind FolderRepository::class
}

val useCaseModule = module {
    singleOf(::SearchBooksUseCase)
    singleOf(::GetBookshelfUseCase)
    singleOf(::SaveBookUseCase)
    singleOf(::DeleteBookUseCase)
    singleOf(::UpdateBookStatusUseCase)
    singleOf(::UpdateBookNotesUseCase)
    single {
        BookUseCases(
            searchBooks = get(),
            getBookshelf = get(),
            saveBook = get(),
            deleteBook = get(),
            updateBookStatus = get(),
            updateBookNotes = get()
        )
    }
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::ExploreViewModel)
    viewModelOf(::BookDetailViewModel)
    viewModelOf(::AIAssistantViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::LetterboxViewModel)
    viewModelOf(::FolderDetailViewModel)
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