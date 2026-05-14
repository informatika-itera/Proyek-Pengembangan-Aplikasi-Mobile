package com.example.foodsaver.core.di

import com.example.foodsaver.core.util.DatabaseDriverFactory
import com.example.foodsaver.data.local.datastore.DataStoreFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific Koin module for FoodSaver.
 */
val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { DataStoreFactory(androidContext()) }
}
