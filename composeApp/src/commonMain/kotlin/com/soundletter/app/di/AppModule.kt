package com.soundletter.app.di

import com.soundletter.app.core.network.GeminiService
import com.soundletter.app.core.network.HttpClientFactory
import com.soundletter.app.core.util.DatabaseDriverFactory
import com.soundletter.app.data.local.SoundLetterDatabase
import com.soundletter.app.data.repository.LetterRepositoryImpl
import com.soundletter.app.data.repository.MusicRepositoryImpl
import com.soundletter.app.data.repository.UserRepositoryImpl
import com.soundletter.app.data.repository.PreferenceRepositoryImpl
import com.soundletter.app.domain.repository.LetterRepository
import com.soundletter.app.domain.repository.MusicRepository
import com.soundletter.app.domain.repository.UserRepository
import com.soundletter.app.domain.repository.PreferenceRepository
import com.soundletter.app.presentation.screens.compose.ComposeViewModel
import com.soundletter.app.presentation.screens.detail.DetailMessageScreenViewModel
import com.soundletter.app.presentation.screens.history.HistoryScreenViewModel
import com.soundletter.app.presentation.screens.home.HomeScreenViewModel
import com.soundletter.app.presentation.screens.search.SearchScreenViewModel
import com.soundletter.app.presentation.screens.splash.SplashScreenViewModel
import com.soundletter.app.presentation.screens.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        SoundLetterDatabase(driverFactory.createDriver())
    }
    single { HttpClientFactory.create(enableLogging = true) }
    single { GeminiService(get()) }
}

val repositoryModule = module {
    // Menggunakan LetterRepository (Bukan SoundLetterRepository)
    single<LetterRepository> { 
        LetterRepositoryImpl(database = get())
    }
    
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::MusicRepositoryImpl) bind MusicRepository::class
    singleOf(::PreferenceRepositoryImpl) bind PreferenceRepository::class
}

val viewModelModule = module {
    // Injeksi ViewModel per layar
    viewModelOf(::SplashScreenViewModel)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::SearchScreenViewModel)
    viewModelOf(::ComposeViewModel)
    viewModelOf(::DetailMessageScreenViewModel)
    viewModelOf(::HistoryScreenViewModel)
    viewModelOf(::SettingsViewModel)
}

val appModule = listOf(dataModule, repositoryModule, viewModelModule)
