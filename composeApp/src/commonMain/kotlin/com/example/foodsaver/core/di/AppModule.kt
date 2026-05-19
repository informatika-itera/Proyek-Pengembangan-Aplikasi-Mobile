package com.example.foodsaver.core.di

import app.cash.sqldelight.ColumnAdapter
import com.example.foodsaver.core.network.HttpClientFactory
import com.example.foodsaver.data.local.FoodItemEntity
import com.example.foodsaver.data.local.FoodSaverDatabase
import com.example.foodsaver.data.remote.api.GeminiService
import com.example.foodsaver.data.repository.AIRepositoryImpl
import com.example.foodsaver.data.repository.NoteRepositoryImpl
import com.example.foodsaver.domain.repository.AIRepository
import com.example.foodsaver.domain.repository.NoteRepository
import com.example.foodsaver.domain.usecase.*
import com.example.noteai.presentation.screens.addnote.AddNoteViewModel
import com.example.noteai.presentation.screens.ai.AIAssistantViewModel
import com.example.noteai.presentation.screens.detail.NoteDetailViewModel
import com.example.noteai.presentation.screens.home.HomeViewModel
import kotlinx.datetime.Instant
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Adapter untuk mengubah tipe Instant (Kotlinx Datetime) menjadi Long (SQL)
 */
val instantAdapter = object : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)
    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
}

val commonModule = module {
    // Database
    single { 
        FoodSaverDatabase(
            driver = get(),
            FoodItemEntityAdapter = FoodItemEntity.Adapter(
                expiryDateAdapter = instantAdapter
            )
        ) 
    }
    
    // Network
    single { HttpClientFactory().create() }
    single { GeminiService(get()) }

    // Repositories
    single<AIRepository> { AIRepositoryImpl(get()) }
    single<NoteRepository> { NoteRepositoryImpl(get()) }
    
    // Use Cases
    factory { GetAllNotesUseCase(get()) }
    factory { SearchNotesUseCase(get()) }
    factory { SaveNoteUseCase(get()) }
    factory { DeleteNoteUseCase(get()) }
    factory { SummarizeNoteUseCase(get()) }
    factory { ImproveWritingUseCase(get()) }
    factory { GenerateIdeasUseCase(get()) }

    // ViewModels
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { AddNoteViewModel(get(), get()) }
    viewModel { NoteDetailViewModel(get(), get()) }
    viewModel { AIAssistantViewModel(get(), get(), get(), get()) }
}

/**
 * Initialize Koin for all platforms.
 */
fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(commonModule + platformModules)
    }
}
