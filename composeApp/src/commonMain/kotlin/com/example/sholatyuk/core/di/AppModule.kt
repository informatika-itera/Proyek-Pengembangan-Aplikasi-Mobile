package com.example.sholatyuk.core.di

import com.example.sholatyuk.core.network.HttpClientFactory
import com.example.sholatyuk.core.util.DatabaseDriverFactory
import com.example.sholatyuk.data.local.SholatYukDatabase
import com.example.sholatyuk.data.local.datastore.DataStoreFactory
import com.example.sholatyuk.data.local.datastore.create
import com.example.sholatyuk.data.remote.api.GeminiService
import com.example.sholatyuk.data.remote.api.AladhanService
import com.example.sholatyuk.data.repository.AIRepositoryImpl
import com.example.sholatyuk.data.repository.PrayerRepositoryImpl
import com.example.sholatyuk.data.repository.KajianRepositoryImpl
import com.example.sholatyuk.domain.repository.AIRepository
import com.example.sholatyuk.domain.repository.PrayerRepository
import com.example.sholatyuk.domain.repository.KajianRepository
import com.example.sholatyuk.presentation.screens.home.HomeViewModel
import com.example.sholatyuk.presentation.screens.islamai.IslamAIViewModel
import com.example.sholatyuk.presentation.screens.prayer.PrayerViewModel
import com.example.sholatyuk.presentation.screens.doa.DoaViewModel
import com.example.sholatyuk.presentation.screens.profile.ProfileViewModel
import com.example.sholatyuk.presentation.screens.kajian.KajianViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val sharedModules = module {

    // ── Network & Services ───────────────────────────────────────
    single { HttpClientFactory.create() }
    single { GeminiService(get()) }
    single { AladhanService(get()) }

    // ── Database ─────────────────────────────────────────────────
    single {
        SholatYukDatabase(
            driver = get<DatabaseDriverFactory>().createDriver()
        )
    }

    // ── DataStore ─────────────────────────────────────────────────
    single { get<DataStoreFactory>().create() }

    // ── Repositories ──────────────────────────────────────────────
    single<AIRepository> { AIRepositoryImpl(get(), get()) }
    single<PrayerRepository> { PrayerRepositoryImpl(get(), get()) }
    single<KajianRepository> { KajianRepositoryImpl(get()) }

    // ── ViewModels ────────────────────────────────────────────────
    factory { HomeViewModel(get(), get()) }
    factory { PrayerViewModel(get(), get()) }
    factory { IslamAIViewModel(get()) }
    factory { DoaViewModel() }
    factory { KajianViewModel(get()) }
    single { ProfileViewModel() }
}

fun initKoin(
    platformModules: List<org.koin.core.module.Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}