package com.example.musickeep.core.di

import com.example.musickeep.core.util.DatabaseDriverFactory
import com.example.musickeep.data.local.datastore.DataStoreFactory
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory() }
    single { DataStoreFactory() }
}
