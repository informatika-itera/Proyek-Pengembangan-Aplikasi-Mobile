package com.studyhub.core.di

import com.studyhub.core.network.createHttpClient
import com.studyhub.data.local.DatabaseDriverFactory
import com.studyhub.database.StudyHubDatabase
import com.studyhub.data.local.LocalSubjectDataSource
import com.studyhub.data.local.LocalTaskDataSource
import com.studyhub.data.local.AiCacheDataSource
import com.studyhub.data.remote.GroqApiClient
import com.studyhub.data.repository.SubjectRepositoryImpl
import com.studyhub.data.repository.TaskRepositoryImpl
import com.studyhub.data.repository.AiRepositoryImpl
import com.studyhub.domain.repository.SubjectRepository
import com.studyhub.domain.repository.TaskRepository
import com.studyhub.domain.repository.AiRepository
import com.studyhub.domain.usecase.task.*
import com.studyhub.domain.usecase.subject.*
import com.studyhub.domain.usecase.ai.*
import com.studyhub.domain.usecase.preferences.*
import com.studyhub.data.local.PreferencesDataSource
import com.studyhub.domain.repository.PreferencesRepository
import com.studyhub.data.repository.PreferencesRepositoryImpl
import com.studyhub.presentation.theme.ThemeViewModel
import com.studyhub.presentation.screens.home.HomeViewModel
import com.studyhub.presentation.screens.task.TasksViewModel
import com.studyhub.presentation.screens.task.AddEditTaskViewModel
import com.studyhub.presentation.screens.task.TaskDetailViewModel
import com.studyhub.presentation.screens.calendar.CalendarViewModel
import com.studyhub.presentation.screens.profile.ProfileViewModel
import com.studyhub.presentation.screens.ai.SmartPriorityViewModel
import com.studyhub.presentation.screens.ai.SmartReminderViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.koin.core.context.startKoin

// ==================== NETWORK MODULE ====================

val networkModule = module {
    single { createHttpClient() }
    single { GroqApiClient(get()) }
}

// ==================== DATABASE MODULE ====================

val databaseModule = module {
    single { StudyHubDatabase(get<DatabaseDriverFactory>().createDriver()) }
    single { LocalTaskDataSource(get()) }
    single { LocalSubjectDataSource(get()) }
    single { AiCacheDataSource(get()) }
}

// ==================== PREFERENCES MODULE ====================

val preferencesModule = module {
    single { PreferencesDataSource(get()) }
}

// ==================== REPOSITORY MODULE ====================

val repositoryModule = module {
    single<TaskRepository> { TaskRepositoryImpl(get()) }
    single<SubjectRepository> { SubjectRepositoryImpl(get()) }
    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
    single<AiRepository> { AiRepositoryImpl(get(), get()) }
}

// ==================== USE CASE MODULE ====================

val useCaseModule = module {
    factory { AddTaskUseCase(get()) }
    factory { GetAllTasksUseCase(get()) }
    factory { GetActiveTasksUseCase(get()) }
    factory { GetTaskByIdUseCase(get()) }
    factory { GetTasksByDateUseCase(get()) }
    factory { UpdateTaskUseCase(get()) }
    factory { UpdateTaskStatusUseCase(get()) }
    factory { DeleteTaskUseCase(get()) }
    factory { FilterAndSortTasksUseCase() }
    factory { GetAllSubjectsUseCase(get()) }
    factory { AddSubjectUseCase(get()) }
    factory { GetDarkModeUseCase(get()) }
    factory { SetDarkModeUseCase(get()) }
    factory { GetUserPreferencesUseCase(get()) }
    
    // AI Use Cases
    factory { GetSmartPriorityUseCase(get(), get()) }
    factory { GetSmartReminderUseCase(get(), get()) }
    factory { GetAiUsageStatsUseCase(get()) }
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::TasksViewModel)
    viewModelOf(::AddEditTaskViewModel)
    viewModelOf(::TaskDetailViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ThemeViewModel)
    viewModelOf(::SmartPriorityViewModel)
    viewModelOf(::SmartReminderViewModel)
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
