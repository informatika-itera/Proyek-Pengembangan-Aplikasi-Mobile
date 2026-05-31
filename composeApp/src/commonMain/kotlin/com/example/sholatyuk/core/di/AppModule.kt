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
import com.example.sholatyuk.domain.repository.AIRepository
import com.example.sholatyuk.domain.repository.PrayerRepository
import com.example.sholatyuk.presentation.screens.home.HomeViewModel
import com.example.sholatyuk.presentation.screens.islamai.IslamAIViewModel
import com.example.sholatyuk.presentation.screens.prayer.PrayerViewModel
import com.example.sholatyuk.presentation.screens.doa.DoaViewModel
// 👇 1. Tambahkan import ProfileViewModel di sini
import com.example.sholatyuk.presentation.screens.profile.ProfileViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val sharedModules = module {

    // ── Network & Services ───────────────────────────────────────
    single { HttpClientFactory.create() }
    single { GeminiService(get()) }
    single { AladhanService(get()) }
    // LocationService DIHAPUS dari sini karena ini area commonMain

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

    // ── ViewModels ────────────────────────────────────────────────
    factory { HomeViewModel(get(), get()) }
    factory { PrayerViewModel(get(), get()) }
    factory { IslamAIViewModel(get()) }
    factory { DoaViewModel() }
    // 👇 2. Daftarkan ProfileViewModel sebagai single (abadi) di sini!
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