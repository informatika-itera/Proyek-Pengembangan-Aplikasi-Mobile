package com.example.todomaster.core.di

import com.example.todomaster.presentation.screens.home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import com.example.todomaster.core.network.HttpClientFactory
import com.example.todomaster.core.util.DatabaseDriverFactory
import com.example.todomaster.data.local.datastore.DataStoreFactory
import com.example.todomaster.data.local.datastore.UserPreferences
import com.example.todomaster.data.local.datastore.create
import com.example.todomaster.data.remote.api.GeminiService
import com.example.todomaster.data.repository.AIRepositoryImpl
import com.example.todomaster.data.repository.TaskRepositoryImpl
import com.example.todomaster.data.local.TaskDatabase
import com.example.todomaster.domain.repository.AIRepository
import com.example.todomaster.domain.repository.TaskRepository
import com.example.todomaster.domain.usecase.AddTaskUseCase
import com.example.todomaster.presentation.screens.addtask.AddTaskViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import com.example.todomaster.presentation.screens.detail.TaskDetailViewModel
import com.example.todomaster.presentation.screens.quadrantdetail.QuadrantDetailViewModel

// ==================== NETWORK MODULE ====================

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GeminiService)
}

// ==================== DATABASE MODULE ====================

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        TaskDatabase(driverFactory.createDriver())
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
    singleOf(::AIRepositoryImpl) bind AIRepository::class
}

// ==================== USE CASE MODULE ====================

val useCaseModule = module {
    singleOf(::AddTaskUseCase)
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddTaskViewModel)
    viewModelOf(::TaskDetailViewModel)
    viewModelOf(::QuadrantDetailViewModel)
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
