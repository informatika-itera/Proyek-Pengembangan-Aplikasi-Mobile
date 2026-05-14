package com.example.foodsaver.core.di

import app.cash.sqldelight.ColumnAdapter
import com.example.foodsaver.data.local.FoodItemEntity
import com.example.foodsaver.data.local.FoodSaverDatabase
import com.example.foodsaver.core.network.HttpClientFactory
import com.example.foodsaver.core.util.DatabaseDriverFactory
import com.example.foodsaver.data.local.datastore.DataStoreFactory
import com.example.foodsaver.data.local.datastore.UserPreferences
import com.example.foodsaver.data.local.datastore.create
import com.example.noteai.data.remote.api.GeminiService
import com.example.noteai.data.repository.AIRepositoryImpl
import com.example.noteai.data.repository.NoteRepositoryImpl
import com.example.noteai.domain.repository.AIRepository
import com.example.noteai.domain.repository.NoteRepository
import com.example.noteai.domain.usecase.*
import com.example.noteai.presentation.screens.addnote.AddNoteViewModel
import com.example.noteai.presentation.screens.ai.AIAssistantViewModel
import com.example.noteai.presentation.screens.detail.NoteDetailViewModel
import com.example.noteai.presentation.screens.home.HomeViewModel
import kotlinx.datetime.Instant
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
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
        FoodSaverDatabase(
            driver = driverFactory.createDriver(),
            FoodItemEntityAdapter = FoodItemEntity.Adapter(
                expiryDateAdapter = object : ColumnAdapter<Instant, Long> {
                    override fun decode(databaseValue: Long): Instant = 
                        Instant.fromEpochMilliseconds(databaseValue)
                    override fun encode(value: Instant): Long = 
                        value.toEpochMilliseconds()
                }
            )
        )
    }
}

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

val repositoryModule = module {
    singleOf(::NoteRepositoryImpl) bind NoteRepository::class
    singleOf(::AIRepositoryImpl) bind AIRepository::class
}

val useCaseModule = module {
    singleOf(::GetAllNotesUseCase)
    singleOf(::SearchNotesUseCase)
    singleOf(::SaveNoteUseCase)
    singleOf(::DeleteNoteUseCase)
    singleOf(::SummarizeNoteUseCase)
    singleOf(::ImproveWritingUseCase)
    singleOf(::GenerateIdeasUseCase)
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddNoteViewModel)
    viewModelOf(::NoteDetailViewModel)
    viewModelOf(::AIAssistantViewModel)
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
