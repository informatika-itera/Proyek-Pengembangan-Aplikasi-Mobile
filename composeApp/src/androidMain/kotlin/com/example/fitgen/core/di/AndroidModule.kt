package com.example.fitgen.core.di

import com.example.fitgen.core.util.DatabaseDriverFactory
import com.example.fitgen.data.local.datastore.DataStoreFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific Koin module.
 *
 * Menyediakan dependencies yang membutuhkan `Context`:
 * - DatabaseDriverFactory: untuk SQLDelight driver
 * - DataStoreFactory     : untuk lokasi file preferences
 */
val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { DataStoreFactory(androidContext()) }
}
