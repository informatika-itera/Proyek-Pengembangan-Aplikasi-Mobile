package com.example.pantaujompo.core.di

import com.example.pantaujompo.PlatformLocationProvider
import com.example.pantaujompo.core.util.DatabaseDriverFactory
import com.example.pantaujompo.data.local.datastore.DataStoreFactory
import org.koin.dsl.module

val androidModule = module {
    // Kurungnya sudah dikosongkan semua ya bes
    single { DatabaseDriverFactory() }
    single { DataStoreFactory() }
    single { PlatformLocationProvider() }
}