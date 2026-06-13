package com.example.bookku.core.di

import com.example.bookku.core.network.HttpClientFactory
import com.example.bookku.core.util.DatabaseDriverFactory
import com.example.bookku.data.local.BookDatabase
import com.example.bookku.data.local.datastore.DataStoreFactory
import com.example.bookku.data.local.datastore.UserPreferences
import com.example.bookku.data.local.datastore.create
import com.example.bookku.data.remote.api.GeminiService
import com.example.bookku.data.repository.*
import com.example.bookku.domain.repository.*
import com.example.bookku.domain.usecase.*
import com.example.bookku.presentation.screens.addbook.AddBookViewModel
import com.example.bookku.presentation.screens.ai.AIAssistantViewModel
import com.example.bookku.presentation.screens.auth.AuthViewModel
import com.example.bookku.presentation.screens.bookdetail.BookDetailViewModel
import com.example.bookku.presentation.screens.home.HomeViewModel
import com.example.bookku.presentation.screens.forum.ForumViewModel
import com.example.bookku.presentation.screens.tracker.TrackerViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.core.context.startKoin

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
    singleOf(::BookRepositoryImpl) bind NoteRepository::class
    singleOf(::AIRepositoryImpl) bind AIRepository::class
    singleOf(::ReviewRepositoryImpl) bind ReviewRepository::class
    singleOf(::ReadingProgressRepositoryImpl) bind ReadingProgressRepository::class
    singleOf(::CommentRepositoryImpl) bind CommentRepository::class
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
}

// ==================== USE CASE MODULE ====================

val useCaseModule = module {
    singleOf(::GetAllNotesUseCase)
    singleOf(::GetBookByIdUseCase)
    singleOf(::SearchNotesUseCase)
    singleOf(::SaveNoteUseCase)
    singleOf(::deleteBookUseCase)
    singleOf(::SummarizeNoteUseCase)
    singleOf(::ImproveWritingUseCase)
    singleOf(::GenerateIdeasUseCase)
    singleOf(::GetRecommendationUseCase)
    singleOf(::UpdateReadingProgressUseCase)
    singleOf(::CompleteReadingUseCase)
    singleOf(::AddReadingProgressUseCase)
    singleOf(::AddReviewUseCase)
    singleOf(::DeleteReviewUseCase)
    singleOf(::AddCommentUseCase)
    singleOf(::DeleteCommentUseCase)
    singleOf(::LikeCommentUseCase)
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddBookViewModel)
    viewModelOf(::BookDetailViewModel)
    viewModelOf(::AIAssistantViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::ForumViewModel)
    viewModelOf(::TrackerViewModel)
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
