package com.example.masakuy.core.di

import com.example.masakuy.core.network.HttpClientFactory
import com.example.masakuy.core.util.DatabaseDriverFactory
import com.example.masakuy.data.local.database.MasakuyDatabase
import com.example.masakuy.data.mapper.RecipeMapper
import com.example.masakuy.data.repository.AIRepositoryImpl
import com.example.masakuy.data.repository.RecipeRepositoryImpl
import com.example.masakuy.data.repository.RecipeSeeder
import com.example.masakuy.domain.repository.AIRepository
import com.example.masakuy.domain.repository.RecipeRepository
import com.example.masakuy.domain.usecase.GetRecipeDetailUseCase
import com.example.masakuy.domain.usecase.GetRecipesUseCase
import com.example.masakuy.domain.usecase.GetRecommendationUseCase
import com.example.masakuy.domain.usecase.SaveFavoriteUseCase
import com.example.masakuy.presentation.screens.api.GeminiService
import com.example.masakuy.presentation.screens.auth.LoginViewModel
import com.example.masakuy.presentation.screens.detail.DetailViewModel
import com.example.masakuy.presentation.screens.favorite.FavoriteViewModel
import com.example.masakuy.presentation.screens.home.HomeViewModel
import com.example.masakuy.presentation.screens.recommendation.RecommendationViewModel
import com.example.masakuy.presentation.screens.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun appModule() = module {
    single { HttpClientFactory.create() }
    single { GeminiService(get()) }

    single {
        val driver = DatabaseDriverFactory(androidContext()).createDriver()
        MasakuyDatabase(driver).also { RecipeSeeder(it).seedIfEmpty() }
    }

    single { RecipeMapper() }

    single<RecipeRepository> { RecipeRepositoryImpl(database = get(), mapper = get()) }
    single<AIRepository> { AIRepositoryImpl(geminiService = get()) }

    single { GetRecipesUseCase(get()) }
    single { GetRecipeDetailUseCase(get()) }
    single { SaveFavoriteUseCase(get()) }
    single { GetRecommendationUseCase(get(), get()) }

    viewModel { LoginViewModel() }
    viewModel { HomeViewModel(get()) }
    viewModel { RecommendationViewModel(get()) }
    viewModel { DetailViewModel(get(), get(), get()) }
    viewModel { FavoriteViewModel(getRecipesUseCase = get(), saveFavoriteUseCase = get()) }
    viewModel { SearchViewModel(get()) }
}
