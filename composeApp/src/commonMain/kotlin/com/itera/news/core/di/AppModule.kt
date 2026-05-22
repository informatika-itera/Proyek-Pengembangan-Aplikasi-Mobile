package com.itera.news.core.di

import com.itera.news.core.network.createHttpClient
import com.itera.news.data.remote.api.NewsApi
import com.itera.news.data.repository.NewsRepositoryImpl
import com.itera.news.domain.repository.NewsRepository
import com.itera.news.domain.usecase.GetMbgNewsUseCase
import org.koin.dsl.module
import org.koin.compose.viewmodel.dsl.viewModel
import com.itera.news.presentation.screens.home.NewsViewModel
import com.itera.news.presentation.screens.bookmark.BookmarkViewModel
import com.itera.news.presentation.screens.add.AddEditViewModel

const val NEWS_API_KEY = "0faefcb90a144faf99a182e7ca3332d9"

val sharedModule = module {
    single { createHttpClient() }
    single { NewsApi(get(), NEWS_API_KEY) }
    single<NewsRepository> { NewsRepositoryImpl(get(), get()) }
    single { GetMbgNewsUseCase(get()) }
    
    // Baris di bawah ini wajib ada untuk mencegah crash
    viewModel { NewsViewModel(get(), get()) }
    viewModel { BookmarkViewModel(get()) }
    viewModel { AddEditViewModel(get()) }
}
