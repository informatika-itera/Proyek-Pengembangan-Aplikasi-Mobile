package com.example.sholatyuk.core.di

import com.example.sholatyuk.core.network.HttpClientFactory
import com.example.sholatyuk.core.util.DatabaseDriverFactory
import com.example.sholatyuk.data.local.SholatYukDatabase
import com.example.sholatyuk.data.local.datastore.DataStoreFactory
import com.example.sholatyuk.data.local.datastore.create
import com.example.sholatyuk.data.remote.api.GeminiService
import com.example.sholatyuk.presentation.screens.home.HomeViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val sharedModules = module {

    // ── Network ──────────────────────────────────────────────────
    single { HttpClientFactory.create() }
    single { GeminiService(get()) }

    // ── Database ─────────────────────────────────────────────────
    single {
        SholatYukDatabase(
            driver = get<DatabaseDriverFactory>().createDriver()
        )
    }

    // ── DataStore ─────────────────────────────────────────────────
    single { get<DataStoreFactory>().create() }

    // ── ViewModels ────────────────────────────────────────────────
    single { HomeViewModel() }
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