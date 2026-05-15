package com.example.edumate.core.di

import com.example.edumate.data.local.TaskDatabase
import com.example.edumate.data.repository.TaskRepositoryImpl
import com.example.edumate.domain.repository.TaskRepository
import com.example.edumate.presentation.screens.add.AddEditViewModel
import com.example.edumate.presentation.screens.detail.DetailViewModel
import com.example.edumate.presentation.screens.home.HomeViewModel
import com.example.edumate.data.repository.AIRepositoryImpl
import com.example.edumate.domain.repository.AIRepository
import com.example.edumate.presentation.screens.ai.AIAssistantViewModel

// Semua import ini sekarang menggunakan edumate
import com.example.edumate.core.network.HttpClientFactory
import com.example.edumate.core.util.DatabaseDriverFactory
import com.example.edumate.data.local.datastore.DataStoreFactory
import com.example.edumate.data.local.datastore.UserPreferences
import com.example.edumate.data.local.datastore.create
import com.example.edumate.data.remote.api.GeminiService

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GeminiService)
}

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        TaskDatabase(driverFactory.createDriver())
    }
}

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

val repositoryModule = module {
    single<TaskRepository> { TaskRepositoryImpl(get()) }
    singleOf(::AIRepositoryImpl) bind AIRepository::class
}

val viewModelModule = module {
    factory { HomeViewModel(get()) }
    factory { (taskId: Long?) -> AddEditViewModel(get(), taskId) }
    factory { (taskId: Long) -> DetailViewModel(get(), taskId) }
    factory { AIAssistantViewModel(get()) }
}

val sharedModules = listOf(networkModule, databaseModule, preferencesModule, repositoryModule, viewModelModule)

fun initKoin(platformModules: List<Module> = emptyList(), config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}