package com.example.musickeep.core.di

import com.example.musickeep.core.util.DatabaseDriverFactory
import com.example.musickeep.data.local.datastore.DataStoreFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { DataStoreFactory(androidContext()) }
}
