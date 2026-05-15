package com.dailybliss.app.core.di

import com.dailybliss.app.core.util.DatabaseDriverFactory
import com.dailybliss.app.data.local.datastore.DataStoreFactory
import com.dailybliss.app.presentation.util.FileStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific Koin module.
 *
 * Menyediakan dependencies yang membutuhkan `Context`:
 * - DatabaseDriverFactory: untuk SQLDelight driver
 * - DataStoreFactory     : untuk lokasi file preferences
 * - FileStorage          : untuk menyimpan file gambar
 */
val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { DataStoreFactory(androidContext()) }
    single { FileStorage(androidContext()) }
}

