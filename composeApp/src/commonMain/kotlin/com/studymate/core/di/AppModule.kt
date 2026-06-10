package com.studymate.core.di

import com.studymate.core.network.ApiConfig
import com.studymate.core.network.HttpClientFactory
import com.studymate.data.local.StudyMateDatabase
import com.studymate.data.repository.*
import com.studymate.domain.repository.*
import com.studymate.domain.usecase.GetUserProfileUseCase
import com.studymate.domain.usecase.RefineNoteUseCase
import com.studymate.presentation.screens.home.HomeViewModel
import com.studymate.presentation.screens.notes.NotesViewModel
import com.studymate.presentation.screens.profile.ProfileViewModel
import com.studymate.presentation.screens.quiz.QuizViewModel
import com.studymate.presentation.screens.calendar.CalendarViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val networkModule = module {
    single { HttpClientFactory.createHttpClient() }
}

val databaseModule = module {
    single { StudyMateDatabase(driver = get()) }
}

val dataModule = module {
    single<AIRepository> { AIRepositoryImpl(client = get(), apiKey = ApiConfig.geminiApiKey) }
    single<MantraRepository> { MantraRepositoryImpl(database = get()) }
    single<NoteRepository> { NoteRepositoryImpl(database = get()) }
    single<UserProfileRepository> { UserProfileRepositoryImpl(database = get()) }
    single<QuizRepository> { QuizRepositoryImpl(database = get()) }
    single<ActivityRepository> { ActivityRepositoryImpl(database = get()) }
    single<ReminderRepository> { ReminderRepositoryImpl(database = get()) }
    single<AuthRepository> { AuthRepositoryImpl(userProfileRepository = get()) }
}

val useCaseModule = module {
    factory { RefineNoteUseCase(aiRepository = get(), noteRepository = get()) }
    factory { GetUserProfileUseCase(profileRepository = get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(noteRepository = get(), profileRepository = get(), mantraRepository = get()) }
    viewModel { NotesViewModel(noteRepository = get(), refineNoteUseCase = get(), activityRepository = get()) }
    viewModel { ProfileViewModel(profileRepository = get(), activityRepository = get(), authRepository = get()) }
    viewModel { QuizViewModel(aiRepository = get(), noteRepository = get(), quizRepository = get(), activityRepository = get()) }
    viewModel { CalendarViewModel(calendarRepository = get(), reminderRepository = get()) }
}

val appModules = listOf(networkModule, databaseModule, dataModule, useCaseModule, viewModelModule)

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModules)
    }
}
