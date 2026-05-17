package com.example.masakuy.core.di

import com.example.masakuy.core.network.HttpClientFactory
import com.example.masakuy.core.util.DatabaseDriverFactory
import com.example.masakuy.data.local.database.MasakuyDatabase
import com.example.masakuy.data.mapper.RecipeMapper
import com.example.masakuy.data.remote.api.GeminiService
import com.example.masakuy.data.repository.AIRepositoryImpl
import com.example.masakuy.data.repository.RecipeRepositoryImpl
import com.example.masakuy.domain.repository.AIRepository
import com.example.masakuy.domain.repository.RecipeRepository
import com.example.masakuy.domain.usecase.GetRecipesUseCase
import com.example.masakuy.domain.usecase.GetRecommendationUseCase
import com.example.masakuy.domain.usecase.GetRecipeDetailUseCase
import com.example.masakuy.domain.usecase.SaveFavoriteUseCase
import com.example.masakuy.presentation.screens.auth.LoginViewModel
import com.example.masakuy.presentation.screens.home.HomeViewModel
import com.example.masakuy.presentation.screens.recommendation.RecommendationViewModel
import com.example.masakuy.presentation.screens.detail.DetailViewModel
import com.example.masakuy.presentation.screens.favorite.FavoriteViewModel
import com.example.masakuy.presentation.screens.search.SearchViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

fun appModule() = module {
    // Network
    single { HttpClientFactory.create() }
    single { GeminiService(get()) }

    // Database
    single {
        val driver = DatabaseDriverFactory().createDriver()
        MasakuyDatabase(driver)
    }

    // Mapper
    single { RecipeMapper() }

    // Repository
    single<RecipeRepository> {
        RecipeRepositoryImpl(
            database = get(),
            mapper = get()
        )
    }
    single<AIRepository> {
        AIRepositoryImpl(
            geminiService = get()
        )
    }

    // Use Cases
    single { GetRecipesUseCase(get()) }
    single { GetRecipeDetailUseCase(get()) }
    single { SaveFavoriteUseCase(get()) }
    single { GetRecommendationUseCase(get(), get()) }

    // ViewModels
    viewModelOf(::LoginViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::RecommendationViewModel)
    viewModelOf(::DetailViewModel)
    viewModelOf(::FavoriteViewModel)
    viewModelOf(::SearchViewModel)
}