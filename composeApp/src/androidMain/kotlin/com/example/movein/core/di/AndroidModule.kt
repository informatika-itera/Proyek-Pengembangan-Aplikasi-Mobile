package com.example.movein.core.di

import com.example.movein.core.util.DatabaseDriverFactory
import com.example.movein.core.util.AndroidDatabaseDriverFactory
import com.example.movein.data.local.datastore.DataStoreFactory
import com.example.movein.data.local.datastore.AndroidDataStoreFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(androidContext()) }
    single<DataStoreFactory> { AndroidDataStoreFactory(androidContext()) }
}