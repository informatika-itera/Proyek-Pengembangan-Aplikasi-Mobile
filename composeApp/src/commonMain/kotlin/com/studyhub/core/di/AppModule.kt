package com.studyhub.core.di

import com.studyhub.core.network.createHttpClient
import com.studyhub.core.util.DatabaseDriverFactory
import com.studyhub.data.local.StudyHubDatabase
import com.studyhub.data.local.datastore.DataStoreFactory
import com.studyhub.data.local.datastore.UserPreferences
import com.studyhub.data.local.datastore.create
import com.studyhub.data.remote.api.GroqService
import com.studyhub.data.repository.TaskRepositoryImpl
import com.studyhub.domain.repository.TaskRepository
import com.studyhub.presentation.screens.home.HomeViewModel
import com.studyhub.presentation.screens.task_detail.TaskDetailViewModel
import com.studyhub.presentation.screens.add_task.AddTaskViewModel
import com.studyhub.presentation.screens.profile.ProfileViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

// ==================== NETWORK MODULE ====================

val networkModule = module {
    single { createHttpClient() }
    singleOf(::GroqService)
}

// ==================== DATABASE MODULE ====================

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        StudyHubDatabase(driverFactory.createDriver())
    }
}

// ==================== PREFERENCES MODULE ====================

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================

val repositoryModule = module {
    singleOf(::TaskRepositoryImpl) bind TaskRepository::class
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddTaskViewModel)
    viewModelOf(::TaskDetailViewModel)
    viewModelOf(::ProfileViewModel)
}

// ==================== SHARED MODULES ====================

val sharedModules = listOf(
    networkModule,
    databaseModule,
    preferencesModule,
    repositoryModule,
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
