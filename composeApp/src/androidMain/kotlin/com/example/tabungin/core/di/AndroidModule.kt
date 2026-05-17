package com.example.tabungin.core.di

import com.example.tabungin.core.util.DatabaseDriverFactory
import com.example.tabungin.data.local.datastore.DataStoreFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { DataStoreFactory(androidContext()) }
}
