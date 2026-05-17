package com.studymate.core.di

import com.studymate.core.network.HttpClientFactory
import com.studymate.core.util.DatabaseDriverFactory
import com.studymate.core.util.getApiKey
import com.studymate.data.local.StudyMateDatabase
import com.studymate.data.remote.api.GeminiService
import com.studymate.data.repository.AIRepositoryImpl
import com.studymate.data.repository.NoteRepositoryImpl
import com.studymate.data.repository.UserProfileRepositoryImpl
import com.studymate.domain.repository.AIRepository
import com.studymate.domain.repository.NoteRepository
import com.studymate.domain.repository.UserProfileRepository
import com.studymate.domain.usecase.GetUserProfileUseCase
import com.studymate.domain.usecase.RefineNoteUseCase
import com.studymate.presentation.screens.home.HomeViewModel
import com.studymate.presentation.screens.notes.NotesViewModel
import com.studymate.presentation.screens.profile.ProfileViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

val networkModule = module {
    single { HttpClientFactory.createHttpClient() }
    single { GeminiService(client = get(), apiKey = getApiKey()) }
}

val databaseModule = module {
    single { StudyMateDatabase(driver = get()) }
}

val dataModule = module {
    single<AIRepository> { AIRepositoryImpl(geminiService = get()) }
    single<NoteRepository> { NoteRepositoryImpl(database = get()) }
    single<UserProfileRepository> { UserProfileRepositoryImpl(database = get()) }
}

val useCaseModule = module {
    factory { RefineNoteUseCase(aiRepository = get(), noteRepository = get()) }
    factory { GetUserProfileUseCase(profileRepository = get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(noteRepository = get(), profileRepository = get()) }
    viewModel { NotesViewModel(noteRepository = get(), refineNoteUseCase = get()) }
    viewModel { ProfileViewModel(profileRepository = get()) }
}

val appModules = listOf(networkModule, databaseModule, dataModule, useCaseModule, viewModelModule)

fun initKoin(config: (org.koin.core.module.Module.() -> Unit)? = null) {
    startKoin {
        modules(appModules)
    }
}
