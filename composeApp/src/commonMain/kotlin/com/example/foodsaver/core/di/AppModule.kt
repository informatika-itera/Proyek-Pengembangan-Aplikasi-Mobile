package com.example.foodsaver.core.di

import app.cash.sqldelight.ColumnAdapter
import com.example.foodsaver.core.network.HttpClientFactory
import com.example.foodsaver.data.local.FoodItemEntity
import com.example.foodsaver.data.local.FoodSaverDatabase
import com.example.foodsaver.data.local.datastore.UserPreferences
import com.example.foodsaver.data.remote.api.GeminiService
import com.example.foodsaver.data.remote.api.MealApiService
import com.example.foodsaver.data.repository.AIRepositoryImpl
import com.example.foodsaver.data.repository.FoodRepositoryImpl
import com.example.foodsaver.data.repository.MealPlanRepositoryImpl
import com.example.foodsaver.data.repository.RecipeRepositoryImpl
import com.example.foodsaver.domain.repository.AIRepository
import com.example.foodsaver.domain.repository.FoodRepository
import com.example.foodsaver.domain.repository.MealPlanRepository
import com.example.foodsaver.domain.repository.RecipeRepository
import com.example.foodsaver.domain.usecase.*
import com.example.foodsaver.presentation.screens.addfood.AddFoodViewModel
import com.example.foodsaver.presentation.screens.detail.FoodDetailViewModel
import com.example.foodsaver.presentation.screens.home.HomeViewModel
import com.example.foodsaver.presentation.screens.ai.AIAssistantViewModel
import com.example.foodsaver.presentation.screens.expiry.ExpiryViewModel
import com.example.foodsaver.presentation.screens.calendar.CalendarViewModel
import com.example.foodsaver.presentation.screens.profile.ProfileViewModel
import com.example.foodsaver.presentation.screens.mealplan.MealPlannerViewModel
import com.example.foodsaver.presentation.screens.recipe.RecipeViewModel
import com.example.foodsaver.presentation.screens.recipe.CookFromStockViewModel
import com.example.foodsaver.presentation.screens.recipe.detail.RecipeDetailViewModel
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
                buyDateAdapter = instantAdapter,
                expiryDateAdapter = instantAdapter
            )
        ) 
    }
    
    // Preferences
    single { get<com.example.foodsaver.data.local.datastore.DataStoreFactory>().create() }
    single { UserPreferences(get()) }

    // Network
    single { HttpClientFactory().create() }
    single { GeminiService(get()) }
    single { MealApiService(get()) }

    // Repositories
    single<AIRepository> { AIRepositoryImpl(get()) }
    single<FoodRepository> { FoodRepositoryImpl(get()) }
    single<RecipeRepository> { RecipeRepositoryImpl(get(), get()) }
    single<MealPlanRepository> { MealPlanRepositoryImpl(get()) }
    
    // Use Cases (Food)
    factory { GetAllFoodUseCase(get()) }
    factory { GetFoodDetailUseCase(get()) }
    factory { SaveFoodUseCase(get()) }
    factory { DeleteFoodUseCase(get()) }
    
    // Use Cases (Recipe)
    factory { GetRecipesByIngredientsUseCase(get()) }
    factory { GetRecipeDetailsUseCase(get()) }
    factory { SearchRecipesUseCase(get()) }
    factory { ToggleFavoriteRecipeUseCase(get()) }
    factory { GetFavoriteRecipesUseCase(get()) }

    // Use Cases (Meal Plan)
    factory { GetMealPlansForDateUseCase(get()) }
    factory { AddMealPlanUseCase(get()) }
    factory { RemoveMealPlanUseCase(get()) }
    
    // Use Cases (AI)
    factory { SummarizeNoteUseCase(get()) }
    factory { ImproveWritingUseCase(get()) }
    factory { GenerateIdeasUseCase(get()) }

    // ViewModels
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { AddFoodViewModel(get(), get()) }
    viewModel { FoodDetailViewModel(get(), get(), get()) }
    viewModel { AIAssistantViewModel(get(), get(), get(), get()) }
    viewModel { RecipeViewModel(get(), get()) }
    viewModel { RecipeDetailViewModel(get(), get(), get()) }
    viewModel { MealPlannerViewModel(get(), get()) }
    viewModel { ExpiryViewModel(get()) }
    viewModel { CalendarViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { CookFromStockViewModel(get()) }
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
