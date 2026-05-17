package com.example.noteai.core.di

import com.example.noteai.core.network.HttpClientFactory
import com.example.noteai.core.util.DatabaseDriverFactory
import com.example.noteai.data.local.NoteDatabase
import com.example.noteai.data.local.datastore.DataStoreFactory
import com.example.noteai.data.local.datastore.UserPreferences
import com.example.noteai.data.local.datastore.create
import com.example.noteai.data.remote.api.GeminiService
import com.example.noteai.data.repository.AIRepositoryImpl
import com.example.noteai.data.repository.PantryRepositoryImpl
import com.example.noteai.data.repository.RecipeRepositoryImpl
import com.example.noteai.domain.repository.AIRepository
import com.example.noteai.domain.repository.PantryRepository
import com.example.noteai.domain.repository.RecipeRepository
import com.example.noteai.domain.usecase.*
import com.example.noteai.presentation.screens.chat.ChatViewModel
import com.example.noteai.presentation.screens.pantry.PantryViewModel
import com.example.noteai.presentation.screens.recipe.RecipeViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

// ==================== NETWORK MODULE ====================

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GeminiService)
}

// ==================== DATABASE MODULE ====================

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        NoteDatabase(driverFactory.createDriver())
    }
}

// ==================== PREFERENCES MODULE = : Preferences MODULE ====================

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================

val repositoryModule = module {
    singleOf(::PantryRepositoryImpl) bind PantryRepository::class
    singleOf(::RecipeRepositoryImpl) bind RecipeRepository::class
    singleOf(::AIRepositoryImpl) bind AIRepository::class
}

// ==================== USE CASE MODULE ====================

val useCaseModule = module {
    // Pantry
    singleOf(::GetPantryItems)
    singleOf(::AddPantryItem)
    singleOf(::UpdatePantryItem)
    singleOf(::DeletePantryItem)
    singleOf(::UpdateStockAmount)
    
    // Recipe
    singleOf(::GetRecipes)
    singleOf(::GetFavoriteRecipes)
    singleOf(::GetRecipeById)
    singleOf(::AddRecipe)
    singleOf(::UpdateRecipe)
    singleOf(::DeleteRecipe)
    singleOf(::ToggleFavoriteRecipe)
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModelOf(::ChatViewModel)
    viewModelOf(::PantryViewModel)
    viewModelOf(::RecipeViewModel)
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
