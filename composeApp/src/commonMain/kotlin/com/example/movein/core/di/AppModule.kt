package com.example.movein.core.di

import com.example.movein.core.network.HttpClientFactory
import com.example.movein.core.util.DatabaseDriverFactory
import com.example.movein.data.local.datastore.DataStoreFactory
import com.example.movein.data.local.datastore.UserPreferences
import com.example.movein.data.local.datastore.create
import com.example.movein.data.remote.api.GeminiService
import com.example.movein.data.repository.AIRepositoryImpl
import com.example.movein.data.repository.NoteRepositoryImpl
import com.example.movein.domain.repository.AIRepository
import com.example.movein.domain.repository.NoteRepository
import com.example.movein.domain.usecase.DeleteNoteUseCase
import com.example.movein.domain.usecase.GenerateIdeasUseCase
import com.example.movein.domain.usecase.GetAllNotesUseCase
import com.example.movein.domain.usecase.ImproveWritingUseCase
import com.example.movein.domain.usecase.SaveNoteUseCase
import com.example.movein.domain.usecase.SearchNotesUseCase
import com.example.movein.domain.usecase.SummarizeNoteUseCase
import com.example.movein.presentation.auth.AuthViewModel
import com.example.movein.presentation.screens.addnote.AddNoteViewModel
import com.example.movein.presentation.screens.ai.AIAssistantViewModel
import com.example.movein.presentation.screens.detail.NoteDetailViewModel
import com.example.movein.presentation.screens.home.HomeViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    single {
        GeminiService(
            client = get(),
            apiKey = get()
        )
    }
}

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        val driver = driverFactory.createDriver()

        val clazz = Class.forName("com.example.movein.data.local.NoteDatabase")
        val factoryField = clazz.getField("Companion")
        val companionInstance = factoryField.get(null)

        val invokeMethod = companionInstance.javaClass.getMethod("invoke", app.cash.sqldelight.db.SqlDriver::class.java)
        invokeMethod.invoke(companionInstance, driver)
    }
}

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

val repositoryModule = module {
    single<NoteRepository> {
        val driverFactory: DatabaseDriverFactory = get()
        val driver = driverFactory.createDriver()

        val clazz = Class.forName("com.example.movein.data.local.NoteDatabase")
        val factoryField = clazz.getField("Companion")
        val companionInstance = factoryField.get(null)
        val invokeMethod = companionInstance.javaClass.getMethod("invoke", app.cash.sqldelight.db.SqlDriver::class.java)
        val dbInstance = invokeMethod.invoke(companionInstance, driver)

        val repoClazz = Class.forName("com.example.movein.data.repository.NoteRepositoryImpl")
        val constructor = repoClazz.getConstructor(clazz)
        constructor.newInstance(dbInstance) as NoteRepository
    }
    single<AIRepository> { AIRepositoryImpl(geminiService = get()) }
}

val useCaseModule = module {
    single { GetAllNotesUseCase(repository = get()) }
    single { SearchNotesUseCase(repository = get()) }
    single { SaveNoteUseCase(repository = get()) }
    single { DeleteNoteUseCase(repository = get()) }
    single { SummarizeNoteUseCase(get()) }
    single { ImproveWritingUseCase(get()) }
    single { GenerateIdeasUseCase(get()) }
}

val viewModelModule = module {
    single<HomeViewModel> {
        HomeViewModel(
            getAllNotesUseCase = get(),
            searchNotesUseCase = get(),
            deleteNoteUseCase = get(),
            repository = get()
        )
    }

    single<AIAssistantViewModel> {
        AIAssistantViewModel(
            aiRepository = get(),
            summarizeUseCase = get(),
            improveWritingUseCase = get(),
            generateIdeasUseCase = get()
        )
    }

    single<AuthViewModel> {
        val driverFactory: DatabaseDriverFactory = get()
        val driver = driverFactory.createDriver()

        val clazz = Class.forName("com.example.movein.data.local.NoteDatabase")
        val factoryField = clazz.getField("Companion")
        val companionInstance = factoryField.get(null)
        val invokeMethod = companionInstance.javaClass.getMethod("invoke", app.cash.sqldelight.db.SqlDriver::class.java)
        val dbInstance = invokeMethod.invoke(companionInstance, driver)

        val vmClazz = Class.forName("com.example.movein.presentation.auth.AuthViewModel")
        val constructor = vmClazz.getConstructor(clazz)
        constructor.newInstance(dbInstance) as AuthViewModel
    }

    single<AddNoteViewModel> {
        AddNoteViewModel(
            repository = get(),
            saveNoteUseCase = get()
        )
    }

    single<NoteDetailViewModel> {
        NoteDetailViewModel(
            repository = get(),
            deleteNoteUseCase = get()
        )
    }
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