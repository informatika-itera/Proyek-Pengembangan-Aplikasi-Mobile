package com.studymate.core.di

import com.studymate.core.network.HttpClientFactory
import com.studymate.core.util.DatabaseDriverFactory
import com.studymate.core.util.getApiKey
import com.studymate.data.local.StudyMateDatabase
import com.studymate.data.repository.AIRepositoryImpl
import com.studymate.data.repository.ActivityRepositoryImpl
import com.studymate.data.repository.AuthRepositoryImpl
import com.studymate.data.repository.NoteRepositoryImpl
import com.studymate.data.repository.QuizRepositoryImpl
import com.studymate.data.repository.ReminderRepositoryImpl
import com.studymate.data.repository.UserProfileRepositoryImpl
import com.studymate.domain.repository.AIRepository
import com.studymate.domain.repository.ActivityRepository
import com.studymate.domain.repository.AuthRepository
import com.studymate.domain.repository.NoteRepository
import com.studymate.domain.repository.QuizRepository
import com.studymate.domain.repository.ReminderRepository
import com.studymate.domain.repository.UserProfileRepository
import com.studymate.domain.usecase.GetUserProfileUseCase
import com.studymate.domain.usecase.RefineNoteUseCase
import com.studymate.presentation.screens.calendar.CalendarViewModel
import com.studymate.presentation.screens.home.HomeViewModel
import com.studymate.presentation.screens.notes.NotesViewModel
import com.studymate.presentation.screens.profile.ProfileViewModel
import com.studymate.presentation.screens.quiz.QuizViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val networkModule = module {
    single { HttpClientFactory.createHttpClient() }
}

val databaseModule = module {
    single { StudyMateDatabase(driver = get()) }
}

val dataModule = module {
    single<AIRepository> { 
        AIRepositoryImpl(
            client = get(),
            groqApiKey = getApiKey()
        )
    }
    single<NoteRepository> { NoteRepositoryImpl(database = get()) }
    single<UserProfileRepository> { UserProfileRepositoryImpl(database = get()) }
    single<ActivityRepository> { ActivityRepositoryImpl(database = get()) }
    single<QuizRepository> { QuizRepositoryImpl(database = get()) }
    single<AuthRepository> { AuthRepositoryImpl(userProfileRepository = get()) }
    single<ReminderRepository> { ReminderRepositoryImpl(database = get()) }
    single<com.studymate.domain.repository.PreferenceRepository> { 
        com.studymate.data.repository.PreferenceRepositoryImpl(database = get()) 
    }
}

val useCaseModule = module {
    factory { RefineNoteUseCase(aiRepository = get(), noteRepository = get()) }
    factory { GetUserProfileUseCase(profileRepository = get()) }
}

val viewModelModule = module {
    viewModel { com.studymate.presentation.AppViewModel(preferenceRepository = get()) }
    viewModel { HomeViewModel(noteRepository = get(), profileRepository = get()) }
    viewModel { 
        NotesViewModel(
            noteRepository = get(), 
            refineNoteUseCase = get(), 
            activityRepository = get()
        ) 
    }
    viewModel { 
        ProfileViewModel(
            profileRepository = get(), 
            activityRepository = get(), 
            authRepository = get(), 
            reminderRepository = get()
        ) 
    }
    viewModel { 
        QuizViewModel(
            aiRepository = get(), 
            noteRepository = get(),
            quizRepository = get(),
            activityRepository = get()
        ) 
    }
    viewModel { 
        CalendarViewModel(
            calendarRepository = get(), 
            reminderRepository = get()
        ) 
    }
}

val appModules = listOf(networkModule, databaseModule, dataModule, useCaseModule, viewModelModule)

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModules)
    }
}
